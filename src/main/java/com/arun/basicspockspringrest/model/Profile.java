package com.arun.basicspockspringrest.model;

import lombok.*;

/**
 * @author arun on 7/28/20
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Profile {
    private Long id;
    private String uuid;
    private String email;
    private String tokenId;
    private String first_name;
    private String last_name;
    private String url;
}
