package com.personal.pojo.bo;

import lombok.Data;

/**
 * 用户新增或修改地址的BO
 */
@Data
public class AddressBO {

    private String addressId;

    private String userId;
    private String receiver;
    private String mobile;
    private String province;
    private String city;
    private String district;
    private String detail;


}
