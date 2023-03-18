package com.capstone.foodify.Model.Response;

import com.capstone.foodify.Model.Address;
import com.capstone.foodify.Model.Page;

import java.util.List;

public class Addresses {
    private List<Address> addresses;
    private Page page;

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
