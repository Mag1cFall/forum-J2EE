package com.forum.view;

import com.forum.model.Board;
import com.forum.model.Comment;
import com.forum.model.Post;
import com.forum.model.User;
import com.forum.service.BoardService;
import com.forum.service.PostService;
import com.forum.service.UserService;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * 控制台视图类
 * 负责处理所有与用户的交互、菜单显示和输入输出。
 * @author 任航瑞
 * @date 2025-06-27
 */
public class ConsoleView {
    // ANSI Color Codes for styling the console output
    private static final String RESET = "\u001B[0m"; // 重置所有样式
    private static final String BOLD = "\u001B[1m"; // 粗体
    private static final String CYAN = "\u001B[36m"; // 青色
    private static final String YELLOW = "\u001B[33m"; // 黄色
    private static final String GREEN = "\u001B[32m"; // 绿色
    private static final String RED = "\u001B[31m"; // 红色
    private static final String MAGENTA = "\u001B[35m"; // 品红色
    private static final String GRAY = "\u001B[90m"; // 灰色

    // --- Service层依赖 ---
    private final UserService userService; // 用户服务
    private final BoardService boardService; // 版块服务
    private final PostService postService; // 帖子服务
    
    // --- 内部状态 ---
    private final Scanner scanner; // 用于读取用户输入
    private User currentUser; // 当前登录的用户

    // 构造函数，通过依赖注入初始化所有服务
    public ConsoleView(UserService userService, BoardService boardService, PostService postService) {
        this.userService = userService;
        this.boardService = boardService;
        this.postService = postService;
        this.scanner = new Scanner(System.in); // 初始化Scanner
        this.currentUser = null; // 初始状态为未登录
    }

    // 程序主循环
    public void run() {
        printHeader("欢迎来到论坛!"); // 打印欢迎头
        while (true) { // 无限循环，直到用户选择退出
            if (currentUser == null) { // 根据是否登录显示不同菜单
                showMainMenu(); // 显示主菜单（未登录）
            } else {
                showUserMenu(); // 显示用户菜单（已登录）
            }
        }
    }

    // 显示主菜单（未登录状态）
    private void showMainMenu() {
        printHeader("主菜单"); // 打印菜单标题
        printMenu("登录", "注册", "退出"); // 打印菜单项
        System.out.print(BOLD + "> " + RESET); // 打印输入提示符
        int choice = readIntInput(); // 读取用户选择

        switch (choice) { // 根据选择执行不同操作
            case 1: handleLogin(); break; // 处理登录
            case 2: handleRegister(); break; // 处理注册
            case 3:
                System.out.println(GREEN + "感谢使用，再见!" + RESET);
                System.exit(0); // 正常退出程序
                break;
            default: System.out.println(RED + "无效的输入，请重试。" + RESET); // 处理无效输入
        }
    }

    // 显示用户菜单（已登录状态）
    private void showUserMenu() {
        String userInfo = "你好, " + CYAN + BOLD + currentUser.getUsername() + RESET + " (" + MAGENTA + currentUser.getRole() + RESET + ")!"; // 格式化用户信息
        printHeader("用户菜单", userInfo); // 打印带用户信息的标题
        if (currentUser.getRole() == User.UserRole.ADMIN) { // 如果是管理员
            printMenu("查看版块", "搜索帖子", "个人中心", "管理员菜单", "登出"); // 显示管理员菜单
        } else {
            printMenu("查看版块", "搜索帖子", "个人中心", "登出"); // 显示普通用户菜单
        }
        System.out.print(BOLD + "> " + RESET);
        int choice = readIntInput(); // 读取选择

        switch (choice) { // 根据选择执行操作
            case 1: handleListBoards(); break;
            case 2: handleSearchPosts(); break;
            case 3: handleProfileMenu(); break;
            case 4:
                if (currentUser.getRole() == User.UserRole.ADMIN) handleAdminMenu(); // 管理员的第4项是管理菜单
                else handleLogout(); // 普通用户的第4项是登出
                break;
            case 5:
                if (currentUser.getRole() == User.UserRole.ADMIN) handleLogout(); // 管理员的第5项是登出
                else System.out.println(RED + "无效的输入。" + RESET);
                break;
            default: System.out.println(RED + "无效的输入。" + RESET);
        }
    }

    // 处理用户登出
    private void handleLogout() {
        currentUser = null; // 将当前用户设为null
        System.out.println(GREEN + "已成功登出。" + RESET);
    }

    // 处理用户注册
    private void handleRegister() {
        System.out.print("请输入用户名: ");
        String username = scanner.nextLine(); // 读取用户名
        System.out.print("请输入密码: ");
        String password = scanner.nextLine(); // 读取密码
        try {
            userService.register(username, password); // 调用服务层进行注册
            System.out.println("注册成功!");
        } catch (IllegalArgumentException e) { // 捕获业务逻辑异常
            System.out.println("注册失败: " + e.getMessage()); // 打印友好的错误信息
        }
    }

    // 处理用户登录
    private void handleLogin() {
        System.out.print("请输入用户名: ");
        String username = scanner.nextLine();
        System.out.print("请输入密码: ");
        String password = scanner.nextLine();
        Optional<User> userOpt = userService.login(username, password); // 调用服务层进行登录
        if (userOpt.isPresent()) { // 如果返回的Optional不为空
            currentUser = userOpt.get(); // 设置当前登录用户
            System.out.println("登录成功! 欢迎你, " + currentUser.getUsername());
        } else {
            System.out.println("登录失败: 用户名或密码错误，或账户已被封禁。");
        }
    }

    // 处理浏览版块列表的循环
    private void handleListBoards() {
        while (true) {
            handleListBoardsInternal(); // 显示所有版块
            System.out.print("请输入要进入的版块ID (输入0返回): ");
            int boardId = readIntInput();
            if (boardId > 0) {
                handleViewBoard(boardId); // 进入指定版块
            } else if (boardId == 0) {
                return; // 返回上一级
            }
        }
    }

    // 处理查看单个版块内容的循环
    private void handleViewBoard(int boardId) {
        while (true) {
            Optional<Board> boardOpt = boardService.findById(boardId); // 查找版块
            if (boardOpt.isEmpty()) { // 如果版块不存在
                System.out.println(RED + "版块不存在。" + RESET);
                return;
            }
            Board board = boardOpt.get();
            String moderatorName = (board.getModerator() != null) ? board.getModerator().getUsername() : "无"; // 安全地获取版主名
            printHeader(board.getName() + " 版块", "版主: " + moderatorName); // 打印版块标题

            List<Post> posts = postService.getPostsByBoard(boardId); // 获取该版块下的所有帖子
            if (posts.isEmpty()) {
                System.out.println(GRAY + "该版块下还没有帖子。" + RESET);
            } else {
                printPostList(posts); // 打印帖子列表
            }

            printMenu("查看帖子详情", "发表新帖", "返回版块列表"); // 显示版块操作菜单
            System.out.print(BOLD + "> " + RESET);
            int choice = readIntInput();

            switch (choice) {
                case 1:
                    System.out.print("请输入要查看的帖子ID: ");
                    int postId = readIntInput();
                    if (postId > 0) handleViewPost(postId); // 查看帖子详情
                    break;
                case 2: handleCreatePost(boardId); break; // 发表新帖
                case 3: return; // 返回
                default: System.out.println(RED + "无效的输入，请重试。" + RESET);
            }
        }
    }

    // 打印帖子列表
    private void printPostList(List<Post> posts) {
        for (Post post : posts) { // 遍历帖子列表
            String status = post.isPinned() ? (RED + "[置顶] " + RESET) : ""; // 如果置顶则显示红色状态
            System.out.println(String.format("%s%d.%s %s%s", BOLD, post.getId(), RESET, status, post.getTitle())); // 打印ID和标题
            System.out.println(String.format("   %s作者: %s | %s赞: %d%s / %s踩: %d%s%s", // 打印元数据
                    GRAY, post.getAuthor().getUsername(),
                    GREEN, post.getLikes(), RESET,
                    RED, post.getDislikes(), RESET,
                    GRAY
            ));
             System.out.println(GRAY + "   " + "─".repeat(68) + RESET); // 打印分隔线
        }
    }

    // 处理创建新帖子
    private void handleCreatePost(int boardId) {
        System.out.println("\n--- 发表新帖 ---");
        System.out.print("请输入标题: ");
        String title = scanner.nextLine();
        System.out.print("请输入内容: ");
        String content = scanner.nextLine();
        try {
            postService.createPost(title, content, currentUser.getId(), boardId); // 调用服务层创建帖子
            System.out.println("帖子发表成功!");
        } catch (Exception e) {
            System.out.println("发表失败: " + e.getMessage());
        }
    }

    // 处理查看单个帖子详情的循环
    private void handleViewPost(int postId) {
        Optional<Post> postOpt = postService.getPostDetails(postId); // 获取帖子详情
        if (postOpt.isEmpty()) { // 如果帖子不存在
            System.out.println("帖子不存在。");
            return;
        }
        printPostDetails(postOpt.get()); // 首次打印帖子详情

        while (true) { // 进入帖子操作循环
            System.out.println("\n--- 帖子操作 ---");
            System.out.println("1. 发表评论");
            System.out.println("2. 删除帖子");
            System.out.println("3. 删除评论");
            if (currentUser.getRole() == User.UserRole.ADMIN || (postOpt.get().getBoard().getModerator() != null && postOpt.get().getBoard().getModerator().getId() == currentUser.getId())) { // 检查置顶权限
                System.out.println("4. " + (postOpt.get().isPinned() ? "取消置顶" : "置顶帖子"));
            }
            System.out.println("5. 点赞帖子");
            System.out.println("6. 点踩帖子");
            System.out.println("7. 点赞评论");
            System.out.println("8. 点踩评论");
            System.out.println("0. 返回上一级");
            System.out.print("请选择: ");
            int choice = readIntInput();

            if (choice == 0) { // 选择0则返回
                return;
            }

            boolean shouldRefresh = true; // 标记操作后是否需要刷新帖子内容
            switch (choice) {
                case 1:
                    handleAddComment(postId); // 添加评论
                    break;
                case 2:
                    handleDeletePost(postId); // 删除帖子
                    return; // 删除后直接返回上级菜单
                case 3:
                    handleDeleteComment(); // 删除评论
                    break;
                case 4: // 置顶/取消置顶
                    if (currentUser.getRole() == User.UserRole.ADMIN || (postOpt.get().getBoard().getModerator() != null && postOpt.get().getBoard().getModerator().getId() == currentUser.getId())) {
                        handleTogglePinPost(postOpt.get());
                    } else {
                        System.out.println("无权操作。");
                        shouldRefresh = false;
                    }
                    break;
                case 5:
                    handleVote("post", postId, 0); // 点赞帖子
                    break;
                case 6:
                    handleVote("post", postId, 1); // 点踩帖子
                    break;
                case 7:
                    System.out.print("请输入要点赞的评论ID: ");
                    int likeCommentId = readIntInput();
                    if (likeCommentId != -1) {
                        handleVote("comment", likeCommentId, 0);
                    } else {
                        shouldRefresh = false;
                    }
                    break;
                case 8:
                    System.out.print("请输入要点踩的评论ID: ");
                    int dislikeCommentId = readIntInput();
                    if (dislikeCommentId != -1) {
                        handleVote("comment", dislikeCommentId, 1);
                    } else {
                        shouldRefresh = false;
                    }
                    break;
                default:
                    System.out.println("无效的输入，请重试。");
                    shouldRefresh = false;
            }

            if (shouldRefresh) { // 如果需要刷新
                postOpt = postService.getPostDetails(postId); // 重新获取帖子详情
                if (postOpt.isEmpty()) {
                    System.out.println("帖子已被删除。");
                    return;
                }
                printPostDetails(postOpt.get()); // 重新打印
            }
        }
    }

    // 打印帖子详情
    private void printPostDetails(Post post) {
        printPostDetailsBox(post); // 打印帖子信息框

        List<Comment> comments = post.getComments(); // 获取评论列表
        if (comments.isEmpty()) {
            System.out.println(GRAY + "\n--- 暂无评论 ---\n" + RESET);
        } else {
            System.out.println(YELLOW + "\n--- 评论列表 ---\n" + RESET);
            for (Comment comment : comments) { // 遍历顶级评论
                printCommentsRecursively(comment, "", true); // 递归打印
            }
        }
    }

    // 递归打印评论及其回复
    private void printCommentsRecursively(Comment comment, String prefix, boolean isLast) {
        String line = isLast ? "└─" : "├─"; // 根据是否是最后一个子节点，决定使用不同的连接符
        String authorInfo = CYAN + comment.getAuthor().getUsername() + RESET;
        String voteInfo = GREEN + "👍" + comment.getLikes() + RESET + " " + RED + "👎" + comment.getDislikes() + RESET;
        String idInfo = GRAY + "(ID:" + comment.getId() + ")" + RESET;

        System.out.println(prefix + GRAY + line + RESET + " " + authorInfo + " " + idInfo + " " + voteInfo); // 打印作者行
        System.out.println(prefix + GRAY + (isLast ? "  " : "│ ") + RESET + BOLD + comment.getContent() + RESET); // 打印内容行

        List<Comment> replies = comment.getReplies(); // 获取当前评论的回复列表
        for (int i = 0; i < replies.size(); i++) { // 遍历回复
            printCommentsRecursively(replies.get(i), prefix + (isLast ? "  " : "│ "), i == replies.size() - 1); // 递归调用，并调整前缀
        }
    }
    
    // 处理投票（点赞/点踩）
    private void handleVote(String type, int id, int voteType) { // voteType: 0 for like, 1 for dislike
        try {
            if ("post".equals(type)) { // 如果是给帖子投票
                if (voteType == 0) postService.likePost(id, currentUser.getId());
                else postService.dislikePost(id, currentUser.getId());
            } else { // 如果是给评论投票
                if (voteType == 0) postService.likeComment(id, currentUser.getId());
                else postService.dislikeComment(id, currentUser.getId());
            }
            System.out.println("操作成功！");
        } catch (Exception e) {
            System.out.println("操作失败: " + e.getMessage());
        }
    }

    // 处理删除评论
    private void handleDeleteComment() {
        System.out.print("请输入要删除的评论ID: ");
        int commentId = readIntInput();
        if (commentId == -1) return;
        try {
            postService.deleteComment(commentId, currentUser); // 调用服务层删除评论
            System.out.println("评论删除成功!");
        } catch (Exception e) {
            System.out.println("删除失败: " + e.getMessage());
        }
    }

    // 处理删除帖子
    private void handleDeletePost(int postId) {
        try {
            postService.deletePost(postId, currentUser); // 调用服务层删除帖子
            System.out.println("帖子删除成功!");
        } catch (Exception e) {
            System.out.println("删除失败: " + e.getMessage());
        }
    }

    // 处理添加评论
    private void handleAddComment(int postId) {
        System.out.print("您要回复某条评论吗? (输入评论ID, 或直接按Enter发表新评论): ");
        String input = scanner.nextLine();
        Integer parentId = null; // 父评论ID，默认为null
        if (!input.trim().isEmpty()) { // 如果用户有输入
            try {
                parentId = Integer.parseInt(input); // 尝试将输入转为整数
            } catch (NumberFormatException e) {
                System.out.println("无效的评论ID，将作为新评论发表。");
            }
        }

        System.out.print("请输入您的评论: ");
        String content = scanner.nextLine();
        try {
            postService.addComment(content, postId, currentUser.getId(), parentId); // 调用服务层添加评论
            System.out.println("评论发表成功!");
        } catch (Exception e) {
            System.out.println("评论失败: " + e.getMessage());
        }
    }

    // 处理置顶/取消置顶
    private void handleTogglePinPost(Post post) {
        try {
            if (post.isPinned()) { // 如果已置顶
                postService.unpinPost(post.getId(), currentUser); // 调用取消置顶
                System.out.println("帖子已取消置顶。");
            } else { // 如果未置顶
                postService.pinPost(post.getId(), currentUser); // 调用置顶
                System.out.println("帖子已置顶。");
            }
        } catch (Exception e) {
            System.out.println("操作失败: " + e.getMessage());
        }
    }

    // 处理管理员菜单
    private void handleAdminMenu() {
        while (true) {
            printHeader("管理员菜单");
            printMenu("拉黑/解封用户", "创建版块", "任命/撤销版主", "返回");
            System.out.print(BOLD + "> " + RESET);
            int choice = readIntInput();

            switch (choice) {
                case 1: handleToggleUserBlock(); break;
                case 2: handleCreateBoard(); break;
                case 3: handleAssignModerator(); break;
                case 4: return;
                default: System.out.println(RED + "无效的输入。" + RESET);
            }
        }
    }

    // 处理任命版主
    private void handleAssignModerator() {
        handleListBoardsInternal(); // 先列出所有版块
        System.out.print("请输入要管理的版块ID: ");
        int boardId = readIntInput();
        if (boardId == -1) return;
        System.out.print("请输入要任命为版主的用户ID (输入0撤销版主): ");
        int userId = readIntInput();
        if (userId == -1) return;
        try {
            boardService.assignModerator(boardId, userId); // 调用服务层处理
            System.out.println("版主设置成功!");
        } catch (Exception e) {
            System.out.println("操作失败: " + e.getMessage());
        }
    }

    // 内部方法，用于显示版块列表
    private void handleListBoardsInternal() {
        List<Board> boards = boardService.getAllBoards();
        printHeader("当前版块列表");
        if (boards.isEmpty()) {
            System.out.println(GRAY + "当前没有版块。" + RESET);
            return;
        }
        for (Board board : boards) {
            String moderatorName = (board.getModerator() != null) ? CYAN + board.getModerator().getUsername() + RESET : "无";
            System.out.printf("%s%d.%s %s - %s (%s版主: %s%s)\n",
                    BOLD, board.getId(), RESET,
                    GREEN + board.getName() + RESET,
                    board.getDescription(),
                    GRAY, moderatorName, RESET
            );
        }
        System.out.println(GRAY + "------------------------------------------------------------------------" + RESET);
    }

    // 处理创建版块
    private void handleCreateBoard() {
        System.out.print("请输入新版块名称: ");
        String name = scanner.nextLine();
        System.out.print("请输入版块描述: ");
        String desc = scanner.nextLine();
        System.out.print("请输入版主的用户ID: ");
        int modId = readIntInput();
        if (modId == -1) return;
        try {
            boardService.createBoard(name, desc, modId); // 调用服务层创建
            System.out.println("版块创建成功!");
        } catch (Exception e) {
            System.out.println("创建失败: " + e.getMessage());
        }
    }

    // 处理封禁/解封用户
    private void handleToggleUserBlock() {
        System.out.print("请输入要操作的用户ID: ");
        int userId = readIntInput();
        if (userId == -1) return;
        Optional<User> userOpt = userService.findById(userId); // 查找用户
        if (userOpt.isEmpty()) {
            System.out.println("用户不存在。");
            return;
        }
        User user = userOpt.get();
        if (user.getStatus() == User.UserStatus.ACTIVE) { // 如果是活跃状态
            userService.blockUser(userId); // 调用封禁
            System.out.println("用户 " + user.getUsername() + " 已被拉黑。");
        } else { // 如果是封禁状态
            userService.unblockUser(userId); // 调用解封
            System.out.println("用户 " + user.getUsername() + " 已被解封。");
        }
    }

    // 处理搜索帖子
    private void handleSearchPosts() {
        System.out.print("请输入要搜索的帖子标题关键字: ");
        String keyword = scanner.nextLine();
        List<Post> posts = postService.searchPostsByTitle(keyword); // 调用服务层搜索
        System.out.println("\n--- 搜索结果 ---");
        if (posts.isEmpty()) {
            System.out.println("没有找到相关帖子。");
        } else {
            for (Post post : posts) { // 打印搜索结果
                System.out.printf("  ID: %d | %s | 作者: %s | 版块: %s\n",
                        post.getId(), post.getTitle(), post.getAuthor().getUsername(), post.getBoard().getName());
            }
            System.out.println("--------------------");
            System.out.print("请输入要查看的帖子ID (输入0返回): ");
            int postId = readIntInput();
            if (postId > 0) {
                handleViewPost(postId);
            }
        }
    }

    // 处理个人中心菜单
    private void handleProfileMenu() {
        while (true) {
            printHeader("个人中心");
            printMenu("查看我的信息", "修改我的密码", "查看我发表的帖子", "查看我发表的评论", "返回");
            System.out.print(BOLD + "> " + RESET);
            int choice = readIntInput();

            switch (choice) {
                case 1: handleViewMyProfile(); break;
                case 2: handleChangePassword(); break;
                case 3: handleViewMyPosts(); break;
                case 4: handleViewMyComments(); break;
                case 5: return;
                default: System.out.println(RED + "无效的输入。" + RESET);
            }
        }
    }

    // 处理查看个人发表的帖子
    private void handleViewMyPosts() {
        List<Post> myPosts = postService.getPostsByAuthor(currentUser.getId());
        System.out.println("\n--- 我发表的帖子 ---");
        if (myPosts.isEmpty()) {
            System.out.println("你还没有发表过任何帖子。");
        } else {
            for (Post post : myPosts) {
                System.out.printf("  ID: %d | %s | 版块: %s\n", post.getId(), post.getTitle(), post.getBoard().getName());
            }
        }
        System.out.println("--------------------");
    }

    // 处理查看个人发表的评论
    private void handleViewMyComments() {
        List<Comment> myComments = postService.getCommentsByAuthor(currentUser.getId());
        System.out.println("\n--- 我发表的评论 ---");
        if (myComments.isEmpty()) {
            System.out.println("你还没有发表过任何评论。");
        } else {
            for (Comment comment : myComments) {
                System.out.printf("  在帖子 '%s' (ID: %d) 中评论:\n  > %s\n---\n",
                        comment.getPost().getTitle(), comment.getPost().getId(), comment.getContent());
            }
        }
        System.out.println("--------------------");
    }

    // 处理查看个人信息
    private void handleViewMyProfile() {
        System.out.println("\n--- 我的信息 ---");
        System.out.println("用户ID: " + currentUser.getId());
        System.out.println("用户名: " + currentUser.getUsername());
        System.out.println("用户角色: " + currentUser.getRole());
        System.out.println("账号状态: " + currentUser.getStatus());
        System.out.println("注册时间: " + currentUser.getCreatedAt());
        System.out.println("--------------------");
    }

    // 处理修改密码
    private void handleChangePassword() {
        System.out.println("\n--- 修改密码 ---");
        System.out.print("请输入旧密码: ");
        String oldPassword = scanner.nextLine();
        System.out.print("请输入新密码: ");
        String newPassword = scanner.nextLine();
        System.out.print("请再次输入新密码: ");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) { // 检查两次新密码是否一致
            System.out.println("两次输入的新密码不一致，请重试。");
            return;
        }

        try {
            userService.changePassword(currentUser.getId(), oldPassword, newPassword); // 调用服务层修改密码
            System.out.println("密码修改成功！");
        } catch (IllegalArgumentException e) {
            System.out.println("密码修改失败: " + e.getMessage());
        }
    }
    
    // 读取用户输入的整数，并处理异常
    private int readIntInput() {
        try {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) return -1; // 允许直接回车，返回-1作为特殊标记
            return Integer.parseInt(line);
        } catch (InputMismatchException | NumberFormatException e) {
            System.out.println(RED + "无效的输入，请输入一个数字。" + RESET);
            return -1; // 输入无效也返回-1
        }
    }

    // --- UI 辅助方法 ---

    // 打印标题（重载方法）
    private void printHeader(String title) {
        printHeader(title, null);
    }

    // 打印带副标题的标题
    private void printHeader(String title, String subtitle) {
        System.out.println();
        String headerLine = "─".repeat(70);
        System.out.println(YELLOW + BOLD + "» " + CYAN + title + " «" + RESET);
        if (subtitle != null) {
            System.out.println(GRAY + "  " + subtitle + RESET);
        }
        System.out.println(GRAY + headerLine + RESET);
        System.out.println();
    }

    // 打印格式化的菜单
    private void printMenu(String... items) {
        for (int i = 0; i < items.length; i++) {
            System.out.println(String.format("  %s%d.%s %s", BOLD, i + 1, RESET, items[i]));
        }
        System.out.println();
    }
    
    // 打印帖子详情信息框
    private void printPostDetailsBox(Post post) {
        System.out.println();
        System.out.println(BOLD + YELLOW + post.getTitle() + RESET); // 打印帖子标题
        System.out.println(GRAY + "─".repeat(getVisibleLength(post.getTitle()) + 4) + RESET); // 根据标题长度打印下划线
        System.out.println(CYAN + "  作者: " + post.getAuthor().getUsername() + RESET); // 打印作者
        System.out.println(GRAY + "  发布于: " + post.getCreatedAt() + RESET); // 打印发布时间
        System.out.println(GREEN + "  赞: " + post.getLikes() + RESET + " / " + RED + "踩: " + post.getDislikes() + RESET); // 打印赞踩数
        System.out.println();
        System.out.println(post.getContent()); // 打印正文
        System.out.println("\n" + GRAY + "─".repeat(72) + RESET); // 打印内容后的分隔线
    }

    // 计算字符串在控制台的可见宽度（处理中文字符和ANSI码）
    private int getVisibleLength(String text) {
        if (text == null) return 0;
        String cleanText = text.replaceAll("\u001B\\[[;\\d]*m", ""); // 移除所有ANSI转义码
        int width = 0;
        for (char c : cleanText.toCharArray()) {
            // 使用正则表达式判断是否为非单字节字符（如中文、全角符号等）
            if (String.valueOf(c).matches("[^\\x00-\\xff]")) {
                width += 2; // 全角字符宽度计为2
            } else {
                width += 1; // 半角字符宽度计为1
            }
        }
        return width;
    }
}