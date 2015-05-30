/**
 * ****************************************************************************
 * Copyright 2013 Comcast Cable Communications Management, LLC
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */
package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class VMFeed {

    @Expose
    private String page;
    @Expose
    private Integer per_page;
    @Expose
    private Integer pages;
    @Expose
    private Integer total;
    @Expose
    @SerializedName("photos")
    private List<PhotoStory> posts = new ArrayList<PhotoStory>();

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public VMFeed withPage(String page) {
        this.page = page;
        return this;
    }

    public Integer getPer_page() {
        return per_page;
    }

    public void setPer_page(Integer per_page) {
        this.per_page = per_page;
    }

    public VMFeed withPer_page(Integer per_page) {
        this.per_page = per_page;
        return this;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public VMFeed withPages(Integer pages) {
        this.pages = pages;
        return this;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public VMFeed withTotal(Integer total) {
        this.total = total;
        return this;
    }

    public List<PhotoStory> getShots() {
        return posts;
    }

    public void setShots(List<PhotoStory> posts) {
        this.posts = posts;
    }

    public VMFeed withShots(List<PhotoStory> posts) {
        this.posts = posts;
        return this;
    }

}
