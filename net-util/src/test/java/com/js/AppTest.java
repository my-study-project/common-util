package com.js;

import com.js.util.OkHttpClientUtil;
import com.js.util.StartLogo;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        System.out.println(StartLogo.print());
        System.out.println(OkHttpClientUtil.doGet("https://www.baidu.com",new HashMap<>()));
        assertTrue(true);
    }
}
