package com.forum.util;

import com.forum.dao.SensitiveWordDao;
import com.forum.dao.impl.SensitiveWordDaoImpl;
import com.forum.util.DatabaseUtil;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("敏感词过滤工具测试")
class FilterUtilTest {

    @BeforeAll
    static void setUp() throws Exception {
        // 1. 重置数据库
        try (Connection conn = DatabaseUtil.getConnection();
             Reader reader = new BufferedReader(new FileReader("doc/数据库设计.sql"))) {
            ScriptRunner sr = new ScriptRunner(conn);
            sr.runScript(reader);
        }

        // 2. 通过反射强制重新加载FilterUtil中的敏感词列表
        Field field = FilterUtil.class.getDeclaredField("sensitiveWords");
        field.setAccessible(true);
        List<String> sensitiveWordsList = (List<String>) field.get(null);
        sensitiveWordsList.clear();

        SensitiveWordDao sensitiveWordDao = new SensitiveWordDaoImpl();
        List<String> wordsFromDb = sensitiveWordDao.findAllWords();
        sensitiveWordsList.addAll(wordsFromDb);
        sensitiveWordsList.sort(Comparator.comparingInt(String::length).reversed());
    }

    @Test
    @DisplayName("测试正常文本")
    void testFilterWithNormalText() {
        String text = "This is a normal sentence.";
        assertEquals(text, FilterUtil.filter(text));
    }

    @Test
    @DisplayName("测试单个敏感词")
    void testFilterWithSingleSensitiveWord() {
        String text = "This is a badword.";
        assertEquals("This is a *******.", FilterUtil.filter(text));
    }

    @Test
    @DisplayName("测试重叠敏感词（长词优先）")
    void testFilterWithOverlappingWords() {
        String text = "This is about censorship.";
        assertEquals("This is about **********.", FilterUtil.filter(text));
    }

    @Test
    @DisplayName("测试输入为null或空字符串")
    void testFilterWithNullOrEmptyInput() {
        assertNull(FilterUtil.filter(null));
        assertEquals("", FilterUtil.filter(""));
    }
}