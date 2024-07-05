package blossom.project.im.exceptions;

import blossom.project.im.grace.result.ResponseStatusEnum;

/**
 * 优雅的处理异常，统一进行封装
 */
public class GraceException {

    public static void display(ResponseStatusEnum statusEnum) {
        throw new MyCustomException(statusEnum);
    }

}
