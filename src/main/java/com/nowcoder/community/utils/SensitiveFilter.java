package com.nowcoder.community.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    private final String REPLACEMENT = "*";
    private TrieNode root = new TrieNode();

    @PostConstruct
    public void init() {
        // 在Bean创建完成后就执行
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-word.txt");
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is))
        ) {
            String keyword;
            while ((keyword = bufferedReader.readLine()) != null) {
                addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("读取敏感词错误：" + e.getMessage());
        }
    }

    public String filter(String text) {
        if(StringUtils.isBlank(text))
            return null;

        StringBuilder sb = new StringBuilder();

        TrieNode cur = root;
        int start = 0;
        int end = 0;
        while(start<text.length()){
            char ch = text.charAt(end);
            if(isSymbol(ch)){
                if(cur == root) {
                    sb.append(ch);
                    start++;
                }
                end++;
                continue;
            }
            cur=cur.getNextNode(ch);
            if(cur == null){
                // ch不是当前节点的子节点，start往后推，start指向的字符加入
                sb.append(text.charAt(start));
                start++;
                end=start;
                cur = root;
            }
            else{
                if(cur.isEnd){
                    // 是尾结点，start到end改成**
                    sb.append(REPLACEMENT);
                    start = ++end;
                    cur = root;
                }
                else{
                    // 不是尾结点，end往后移
                    if (end+1<text.length())
                        end++;
                }
            }
        }

        return sb.toString();

    }

    private boolean isSymbol(char ch){
        return !CharUtils.isAsciiAlphanumeric(ch) && (ch > 0x9fff || ch < 0x2e80);
    }

    private void addKeyword(String keyword) {
        // 向前缀树加关键词
        int len = keyword.length();
        TrieNode cur = root;
        for (int i = 0; i < len; i++) {
            char ch = keyword.charAt(i);
            TrieNode next = cur.getNextNode(ch);
            if (next != null) {
                // 当前节点有值为ch的子节点
                cur = next;
            } else {
                // 没有就新建一个
                TrieNode node = new TrieNode();
                cur.addNextNode(ch, node);
                cur = node;
            }

            if (i == len - 1)
                cur.setEnd(true);
        }

    }

    private static class TrieNode {

        private Map<Character, TrieNode> nextNode = new HashMap<>();
        private boolean isEnd = false;

        public boolean isEnd() {
            return isEnd;
        }

        public void setEnd(boolean end) {
            isEnd = end;
        }

        public void addNextNode(Character c, TrieNode node) {
            nextNode.put(c, node);
        }

        public TrieNode getNextNode(char ch) {
            return nextNode.getOrDefault(ch, null);
        }
    }
}
