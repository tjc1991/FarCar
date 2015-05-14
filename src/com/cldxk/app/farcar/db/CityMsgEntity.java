package com.cldxk.app.farcar.db;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

//建议加上注解， 混淆后表名不受影响
@Table(name = "citymsg")
public class CityMsgEntity extends EntityBase {
    
	@Column(column = "city_choice_index") // 建议加上注解， 混淆后列名不受影响
    public int city_choice_index;

    @Column(column = "city_choice_name")
    private String city_choice_name;

	public int getCity_choice_index() {
		return city_choice_index;
	}

	public void setCity_choice_index(int city_choice_index) {
		this.city_choice_index = city_choice_index;
	}

	public String getCity_choice_name() {
		return city_choice_name;
	}

	public void setCity_choice_name(String city_choice_name) {
		this.city_choice_name = city_choice_name;
	}
	
    @Override
    public String toString() {
        return "citymsg{" +
                "id=" + getId() +
                ", city_choice_index='" + city_choice_index + '\'' +
                ", city_choice_name='" + city_choice_name + '\''+
                '}';
    }
	

}
