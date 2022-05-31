package com.kong.newcoder.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author shijiu
 */
//帖子
@Data
@Getter
@Setter
public class DiscussPost {
    private int id;
    private int userid;
    private String title;
    private String content;
    private int type;//'0-普通; 1-置顶;',
    private int status; //'0-正常; 1-精华; 2-拉黑;',
    private Date createtime;//创建时间
    //帖子评论数量
    private  int comment_count;
    //帖子分数
    private double score;

    public DiscussPost() {
    }


}
