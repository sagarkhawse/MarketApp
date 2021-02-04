package com.skteam.diyodardayari.models;

import java.util.List;

public class User extends Base_Response{

public int id;
    public String user_id , name, email,image,whatsapp, phone, app_version ,
            shop_name, shop_address, business_desc,
            shop_time, services, category_title, category_id, signup_type , created;
    public List<User> result;



}
