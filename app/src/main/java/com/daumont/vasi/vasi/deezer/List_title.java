package com.daumont.vasi.vasi.deezer;

import com.daumont.vasi.vasi.modele.Title;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Créé par JONATHAN DAUMONT le 01/06/2017.
 */

public class List_title {

    @SerializedName("data")
    @Expose
    private List<Title> data = null;
    @SerializedName("total")
    @Expose
    private Integer total;

    public List<Title> getData() {
        return data;
    }

    public void setData(List<Title> data) {
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
