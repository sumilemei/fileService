package com.example.fileupload.dto.enums;

public enum StorageType {

    LOCAL("local","本地存储"),
    MINIO("minio","minio对象存储");

    private final String code;
    private final String description;

    StorageType(String code, String description){
        this.code = code;
        this.description = description;
    }

    public String getCode(){
        return code;
    }

    public String getDescription(){
        return description;
    }

    public static StorageType fromCode(String code){
        for(StorageType type : StorageType.values()){
            if(type.code.equalsIgnoreCase(code)){
                return type;
            }
        }
        throw new IllegalArgumentException("不支持的存储类型: " + code);
    }


}
