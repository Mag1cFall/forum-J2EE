package com.forum.service.impl; // 定义包名

import com.forum.dao.*; // 导入所有DAO接口
import com.forum.dao.impl.VoteDaoImpl; // 导入VoteDao实现
import com.forum.model.*; // 导入所有模型
import com.forum.service.PostService; // 导入PostService接口
import com.forum.util.FilterUtil; // 导入敏感词过滤工具类

import java.util.List; // 导入List接口
import java.util.Optional; // 导入Optional类

/**
 * PostService接口的实现类
 * @author 任航瑞(添加点赞点踩)
 * @author 陈羽飞
 * @date 2025-06-27
 */
public class PostServiceImpl implements PostService {

    private final PostDao postDao; // 持有PostDao实例
    private final UserDao userDao; // 持有UserDao实例
    private final BoardDao boardDao; // 持有BoardDao实例
    private final CommentDao commentDao; // 持有CommentDao实例
    private final VoteDao voteDao; // 持有VoteDao实例

    // 构造函数，用于依赖注入
    public PostServiceImpl(PostDao postDao, UserDao userDao, BoardDao boardDao, CommentDao commentDao) {
        this.postDao = postDao;
        this.userDao = userDao;
        this.boardDao = boardDao;
        this.commentDao = commentDao;
        this.voteDao = new VoteDaoImpl(); // 直接实例化，简化依赖注入
    }

    @Override // 覆盖接口方法
    public Post createPost(String title, String content, int authorId, int boardId) {
        User author = userDao.findById(authorId).orElseThrow(() -> new IllegalArgumentException("Author not found")); // 验证作者是否存在
        Board board = boardDao.findById(boardId).orElseThrow(() -> new IllegalArgumentException("Board not found")); // 验证版块是否存在
        String filteredTitle = FilterUtil.filter(title); // 过滤标题
        String filteredContent = FilterUtil.filter(content); // 过滤内容
        Post post = new Post(0, filteredTitle, filteredContent, author, board); // 创建帖子对象
        postDao.save(post); // 保存帖子
        return post; // 返回创建好的帖子
    }

    @Override // 覆盖接口方法
    public Optional<Post> getPostDetails(int postId) {
        Optional<Post> postOpt = postDao.findById(postId); // 首先获取帖子本身
        postOpt.ifPresent(post -> { // 如果帖子存在
            List<Comment> allComments = commentDao.findByPostId(postId); // 获取该帖子下的所有评论（扁平列表）
            List<Comment> rootComments = new java.util.ArrayList<>(); // 用于存放顶级评论
            java.util.Map<Integer, Comment> commentMap = new java.util.HashMap<>(); // 用于快速查找评论

            for (Comment c : allComments) { // 第一次遍历：将所有评论放入map中，方便通过ID查找
                commentMap.put(c.getId(), c);
            }

            for (Comment c : allComments) { // 第二次遍历：构建层级关系
                if (c.getParentId() != null) { // 如果这是一个回复
                    Comment parent = commentMap.get(c.getParentId()); // 从map中找到它的父评论
                    if (parent != null) {
                        parent.getReplies().add(c); // 将当前回复添加到父评论的回复列表中
                    }
                } else { // 如果这是一个顶级评论
                    rootComments.add(c); // 直接添加到顶级评论列表
                }
            }
            post.setComments(rootComments); // 将构建好的评论树（只有顶级评论的列表）设置回帖子对象
        });
        return postOpt; // 返回包含完整评论树的帖子
    }

    @Override // 覆盖接口方法
    public List<Post> getPostsByBoard(int boardId) {
        return postDao.findByBoardId(boardId); // 直接委托DAO层处理
    }

    @Override // 覆盖接口方法
    public void deletePost(int postId, User currentUser) {
        Post post = postDao.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found")); // 验证帖子是否存在
        // --- 权限检查 ---
        boolean isOwner = post.getAuthor().getId() == currentUser.getId(); // 检查是否为作者本人
        boolean isAdmin = currentUser.getRole() == User.UserRole.ADMIN; // 检查是否为管理员
        boolean isModerator = post.getBoard().getModerator() != null && post.getBoard().getModerator().getId() == currentUser.getId(); // 检查是否为该版块版主
        
        if (isOwner || isAdmin || isModerator) { // 如果满足任一权限
            postDao.deleteById(postId); // 执行删除
        } else {
            throw new SecurityException("User not authorized to delete this post"); // 否则抛出无权限异常
        }
    }

    @Override // 覆盖接口方法
    public void pinPost(int postId, User currentUser) {
        Post post = postDao.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found")); // 验证帖子是否存在
        // --- 权限检查 ---
        boolean isAdmin = currentUser.getRole() == User.UserRole.ADMIN; // 检查是否为管理员
        boolean isModerator = post.getBoard().getModerator() != null && post.getBoard().getModerator().getId() == currentUser.getId(); // 检查是否为版主

        if (isAdmin || isModerator) { // 如果有权限
            post.setPinned(true); // 设置置顶状态
            postDao.update(post); // 更新数据库
        } else {
            throw new SecurityException("User not authorized to pin this post"); // 否则抛出异常
        }
    }

    @Override // 覆盖接口方法
    public void unpinPost(int postId, User currentUser) {
        Post post = postDao.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found")); // 验证帖子是否存在
        // --- 权限检查，逻辑同上 ---
        boolean isAdmin = currentUser.getRole() == User.UserRole.ADMIN;
        boolean isModerator = post.getBoard().getModerator() != null && post.getBoard().getModerator().getId() == currentUser.getId();

        if (isAdmin || isModerator) { // 如果有权限
            post.setPinned(false); // 取消置顶状态
            postDao.update(post); // 更新数据库
        } else {
            throw new SecurityException("User not authorized to unpin this post"); // 否则抛出异常
        }
    }

    @Override // 覆盖接口方法
    public Comment addComment(String content, int postId, int authorId, Integer parentId) {
        User author = userDao.findById(authorId).orElseThrow(() -> new IllegalArgumentException("Author not found")); // 验证作者是否存在
        Post post = postDao.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found")); // 验证帖子是否存在
        String filteredContent = FilterUtil.filter(content); // 过滤评论内容
        Comment comment = new Comment(0, filteredContent, author, post); // 创建评论对象
        comment.setParentId(parentId); // 设置父评论ID（如果为null，则为顶级评论）
        commentDao.save(comment); // 保存评论
        return comment; // 返回创建好的评论
    }

    @Override // 覆盖接口方法
    public void deleteComment(int commentId, User currentUser) {
        Comment comment = commentDao.findById(commentId).orElseThrow(() -> new IllegalArgumentException("Comment not found")); // 验证评论是否存在
        // --- 权限检查，逻辑同删除帖子 ---
        boolean isOwner = comment.getAuthor().getId() == currentUser.getId();
        boolean isAdmin = currentUser.getRole() == User.UserRole.ADMIN;
        boolean isModerator = comment.getPost().getBoard().getModerator() != null && comment.getPost().getBoard().getModerator().getId() == currentUser.getId();

        if (isOwner || isAdmin || isModerator) { // 如果有权限
            commentDao.deleteById(commentId); // 执行删除
        } else {
            throw new SecurityException("User not authorized to delete this comment"); // 否则抛出异常
        }
    }

    @Override
    public void likePost(int postId, int userId) {
        handleVote(postId, null, userId, Vote.VoteType.LIKE); // 调用统一投票处理方法
    }

    @Override
    public void dislikePost(int postId, int userId) {
        handleVote(postId, null, userId, Vote.VoteType.DISLIKE); // 调用统一投票处理方法
    }

    @Override
    public void likeComment(int commentId, int userId) {
        handleVote(null, commentId, userId, Vote.VoteType.LIKE); // 调用统一投票处理方法
    }

    @Override
    public void dislikeComment(int commentId, int userId) {
        handleVote(null, commentId, userId, Vote.VoteType.DISLIKE); // 调用统一投票处理方法
    }

    // 统一处理点赞/点踩的核心私有方法
    private void handleVote(Integer postId, Integer commentId, int userId, Vote.VoteType newVoteType) {
        // 验证投票目标是否存在，防止对已删除的内容投票
        if (postId != null) {
            postDao.findById(postId).orElseThrow(() -> new IllegalArgumentException("帖子不存在。"));
        }
        if (commentId != null) {
            commentDao.findById(commentId).orElseThrow(() -> new IllegalArgumentException("评论不存在。"));
        }

        Optional<Vote> existingVoteOpt; // 查找用户是否已经投过票
        if (postId != null) {
            existingVoteOpt = voteDao.findByUserIdAndPostId(userId, postId);
        } else {
            existingVoteOpt = voteDao.findByUserIdAndCommentId(userId, commentId);
        }

        if (existingVoteOpt.isPresent()) { // Case 1: 用户已经投过票
            Vote existingVote = existingVoteOpt.get();
            if (existingVote.getVoteType() == newVoteType) { // Case 1.1: 重复操作 (点赞后又点赞)
                voteDao.delete(existingVote.getId()); // 删除投票记录，视为取消操作
                updateCount(postId, commentId, newVoteType, -1); // 对应计数-1
            } else { // Case 1.2: 切换操作 (点赞后点踩)
                updateCount(postId, commentId, existingVote.getVoteType(), -1); // 旧的投票类型计数-1
                updateCount(postId, commentId, newVoteType, +1); // 新的投票类型计数+1
                existingVote.setVoteType(newVoteType); // 更新投票记录中的类型
                voteDao.update(existingVote);
            }
        } else { // Case 2: 用户首次投票
            Vote newVote = new Vote(); // 创建新的投票记录
            newVote.setUserId(userId);
            newVote.setPostId(postId);
            newVote.setCommentId(commentId);
            newVote.setVoteType(newVoteType);
            voteDao.save(newVote); // 保存新的投票记录
            updateCount(postId, commentId, newVoteType, +1); // 对应计数+1
        }
    }

    // 更新帖子或评论的点赞/点踩计数的辅助方法
    private void updateCount(Integer postId, Integer commentId, Vote.VoteType voteType, int delta) {
        if (postId != null) { // 如果是操作帖子
            Post post = postDao.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found")); // 找到帖子
            if (voteType == Vote.VoteType.LIKE) { // 根据类型更新赞/踩
                post.setLikes(post.getLikes() + delta);
            } else {
                post.setDislikes(post.getDislikes() + delta);
            }
            postDao.update(post); // 保存更新后的帖子
        } else if (commentId != null) { // 如果是操作评论
            Comment comment = commentDao.findById(commentId).orElseThrow(() -> new IllegalArgumentException("Comment not found")); // 找到评论
            if (voteType == Vote.VoteType.LIKE) { // 根据类型更新赞/踩
                comment.setLikes(comment.getLikes() + delta);
            } else {
                comment.setDislikes(comment.getDislikes() + delta);
            }
            commentDao.update(comment); // 保存更新后的评论
        }
    }

    @Override
    public List<Post> searchPostsByTitle(String keyword) {
        return postDao.searchByTitle(keyword); // 直接委托DAO层处理
    }

    @Override
    public List<Post> getPostsByAuthor(int authorId) {
        return postDao.findByAuthorId(authorId); // 直接委托DAO层处理
    }

    @Override
    public List<Comment> getCommentsByAuthor(int authorId) {
        return commentDao.findByAuthorId(authorId); // 直接委托DAO层处理
    }
}