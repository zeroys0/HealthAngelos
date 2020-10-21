package com.pattonsoft.pattonutil1_0.views.wheel;

import java.util.List;

/**
 * Created by zhao on 2017/6/19.
 */

public class AddressAdapter implements WheelAdapter {
    List<String> list;
    int getMaximumLength;

    public AddressAdapter(List<String> list) {
        this.list = list;
    }

    public AddressAdapter(List<String> list, int getMaximumLength) {
        this.list = list;
        this.getMaximumLength = getMaximumLength;
    }

    @Override
    public int getItemsCount() {
        return list.size();
    }

    @Override
    public String getItem(int index) {
        if (index >= 0 && index < list.size()) {
            return list.get(index);
        }
        return null;
    }

    @Override
    public int getMaximumLength() {
        return getMaximumLength;
    }
}
