package com.andrukhiv.winetour.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapsModel implements Serializable {

    private int id;
    private int terruarId;
    private String title;
    private String description;
    private String address;
    private String web;
    private String phone;
    private Double lat;
    private Double lng;
    private String navPosition;
    private String photo;
    private String virtPhoto;
    private Boolean showDetails;

    private List<WineCardModel> wineCardModels = null;


    public MapsModel(){
    }

    private List<WineCardModel> wineCardModelList = null;

    public MapsModel(int id, int terruarId, String title, String description, String address, String web, String phone, Double lat, Double lng, String navPosition, String photo, String virtual_photo, boolean showDetails) {
        this.id = id;
        this.terruarId = terruarId;
        this.title = title;
        this.description = description;
        this.address = address;
        this.web = web;
        this.phone = phone;
        this.lat = lat;
        this.lng = lng;
        this.navPosition = navPosition;
        this.photo = photo;
        this.virtPhoto = virtual_photo;
        this.showDetails = showDetails;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTerruarId() {
        return terruarId;
    }

    public void setTerruarId(int terruarId) {
        this.terruarId = terruarId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getNavPosition() {
        return navPosition;
    }

    public void setNavPosition(String navPosition) {
        this.navPosition = navPosition;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getVirtPhoto() {
        return virtPhoto;
    }

    public List<WineCardModel> getWineCardModels() {
        return wineCardModels;
    }

    public void setWineCardModels(List<WineCardModel> wineCardModels) {
        this.wineCardModels = wineCardModels;
    }

    public List<WineCardModel> getWineCardModelList() {
        return wineCardModelList;
    }

    public void setWineCardModelList(List<WineCardModel> wineCardModelList) {
        this.wineCardModelList = wineCardModelList;
    }

    public Boolean getShowDetails() {
        return showDetails;
    }



    public static ArrayList<MapsModel> getMaps() {
        ArrayList<MapsModel> datas = new ArrayList<>();
        datas.add(new MapsModel(1, 1,  "Ужгородський замок", "Дегустаційний зал Ужгородського замку", "м. Ужгород, вул. Капітульна, буд. 33", "uzhgorodcastle.com", "+380673120012", 48.6213056, 22.3037793, "48.6213056, 22.3037793", "storage.googleapis.com/db_sommelier/maps/uzh_zam-min.jpg", null, true)); //"live.staticflickr.com/65535/49869365376_a0e1372c53_o.jpg"));
        datas.add(new MapsModel(2, 1,  "Зал 2", "Дегустаційний зал Ужгородського замку", "м. Ужгород, вул. Капітульна, буд.33", "uzhgorodcastle.com", "+380673120012", 48.6213056, 22.3037793, "48.6213056, 22.3037793", "live.staticflickr.com/65535/49868848263_56f8732cbf_o.jpg", "live.staticflickr.com/65535/49868848263_56f8732cbf_o.jpg", true));
        datas.add(new MapsModel(3, 1,  "Зал 3", "Дегустаційний зал Ужгородського замку", "м. Ужгород, вул. Капітульна, буд.33", "uzhgorodcastle.com", "+380673120012", 48.6213056, 22.3037793, "48.6213056, 22.3037793", "storage.googleapis.com/db_sommelier/maps/uzh_zam-min.jpg", "live.staticflickr.com/65535/49869687307_834911e8a8_o.jpg",true));
        datas.add(new MapsModel(4, 2, "ТМ Французький бульвар", "ТМ Французький бульвар", "м. Одеса, Французький Бульвар, 10", "chizay.com", "+380949183668", 48.6213056, 22.3037793, "48.6213056, 22.3037793", "storage.googleapis.com/db_sommelier/maps/uzh_zam-min.jpg", "live.staticflickr.com/65535/49868848178_9ef2096146_o.jpg", true));
        datas.add(new MapsModel(5, 2,  "ТМ Французький бульвар", "ТМ Французький бульвар", "м. Одеса, Французький Бульвар, 10", "chizay.com", "+380949183668", 48.6213056, 22.3037793, "48.6213056, 22.3037793", "storage.googleapis.com/db_sommelier/maps/uzh_zam-min.jpg", "live.staticflickr.com/65535/49868848323_4765a6ea74_o.jpg",true));
        datas.add(new MapsModel(6, 2,  "ТМ Французький бульвар", "ТМ Французький бульвар", "м. Одеса, Французький Бульвар, 10", "chizay.com", "+380949183668", 48.6213056, 22.3037793, "48.6213056, 22.3037793", "storage.googleapis.com/db_sommelier/maps/uzh_zam-min.jpg", "live.staticflickr.com/65535/49868848288_9280304f75_o.jpg", true));

        return datas;
    }
}

