package com.autowash.autowash_pro.dto.request.article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleRequest {

    @NotBlank(message = "Tieu de khong duoc de trong")
    @Size(max = 200, message = "Tieu de toi da 200 ky tu")
    private String title;

    @NotBlank(message = "Noi dung khong duoc de trong")
    private String content;

    @Size(max = 500, message = "Anh dai dien toi da 500 ky tu")
    private String thumbnailUrl;

    @NotBlank(message = "Danh muc khong duoc de trong")
    @Size(max = 100, message = "Danh muc toi da 100 ky tu")
    private String category;

    private Boolean published;
}
