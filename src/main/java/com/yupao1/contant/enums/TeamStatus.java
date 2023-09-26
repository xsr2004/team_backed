package com.yupao1.contant.enums;

/**
 * @Author：xsr
 * @name：TeamStatus
 * team状态枚举
 * @Date：2023/8/2 14:53
 * @Filename：TeamStatus
 */
public enum TeamStatus {

    PUBLIC(0,"公开"),
    PRIVATE(1,"私密"),
    SECRET(2,"加密");
    private int value;
    private String text;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    //根据传入value返回对应枚举对象
    public static TeamStatus getTeamStatusByValue(Integer value){
        if (value==null){
            return null;
        }
        TeamStatus[] values = TeamStatus.values();
        for(TeamStatus status:values){
            if(status.getValue()==value){
                return status;
            }
        }
        return null;
    }
    TeamStatus(int value, String text) {
        this.value = value;
        this.text = text;
    }

}
