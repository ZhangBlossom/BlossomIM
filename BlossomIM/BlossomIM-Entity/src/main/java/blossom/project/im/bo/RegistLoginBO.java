package blossom.project.im.bo;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

/**
 * @Auther ZhangBlossom
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RegistLoginBO {

    @NotBlank(message = "手机号不能为空")
    @Length(min = 11, max = 11, message = "手机号长度不正确")
    private String mobile;

    @NotBlank(message = "验证码不能为空")
    // @Length(min = 6, max = 6, message = "验证码长度不正确")
    private String smsCode;

    private String nickname;

    // @NotNull
    // @NotEmpty
    // @Min()
    // @Max()
    // @Size
    // @Email
    // @Range
    // private Integer test;
}
