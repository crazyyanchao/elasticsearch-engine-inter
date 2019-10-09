package casia.isi.elasticsearch.operation.search.aircraft;
/**
 * 　　　　　　　 ┏┓       ┏┓+ +
 * 　　　　　　　┏┛┻━━━━━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　 ┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 █████━█████  ┃+
 * 　　　　　　　┃　　　　　　 ┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　 ┃ + +
 * 　　　　　　　┗━━┓　　　 ┏━┛
 * ┃　　  ┃
 * 　　　　　　　　　┃　　  ┃ + + + +
 * 　　　　　　　　　┃　　　┃　Code is far away from     bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ +
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　 ┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━━━┳┓┏┛ + + + +
 * 　　　　　　　　　 ┃┫┫　 ┃┫┫
 * 　　　　　　　　　 ┗┻┛　 ┗┻┛+ + + +
 */

import casia.isi.elasticsearch.model.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.search.aircraft
 * @Description: TODO(配置任务)
 * @date 2019/10/8 15:19
 */
public class ConfigTask {
    // 区域
    private List<Shape> areas;
    // 国家
    private List<String> country;
    // 种类
    private List<String> species;
    // 识别码
    private List<String> identificationCode;

    // --飞机--
    // s模式
    private List<String> modeS;
    // 注册号
    private List<String> registrationNum;

    public ConfigTask(List<Shape> areas, List<String> country, List<String> species, List<String> identificationCode, List<String> modeS, List<String> registrationNum) {
        this.areas = areas;
        this.country = country;
        this.species = species;
        this.identificationCode = identificationCode;
        this.modeS = modeS;
        this.registrationNum = registrationNum;
    }

    public ConfigTask(List<Shape> areas, String country, String species, String identificationCode, String modeS, String registrationNum) {
        this.areas = areas;
        this.country = packParas(country);
        this.species = packParas(species);
        this.identificationCode = packParas(identificationCode);
        this.modeS = packParas(modeS);
        this.registrationNum = packParas(registrationNum);
    }

    private List<String> packParas(String country) {
        List<String> paras = new ArrayList<>();
        paras.add(country);
        return paras;
    }

    public List<String> getModeS() {
        return modeS;
    }

    public void setModeS(List<String> modeS) {
        this.modeS = modeS;
    }

    public List<String> getRegistrationNum() {
        return registrationNum;
    }

    public void setRegistrationNum(List<String> registrationNum) {
        this.registrationNum = registrationNum;
    }

    public List<Shape> getAreas() {
        return areas;
    }

    public void setAreas(List<Shape> areas) {
        this.areas = areas;
    }

    public List<String> getCountry() {
        return country;
    }

    public void setCountry(List<String> country) {
        this.country = country;
    }

    public List<String> getSpecies() {
        return species;
    }

    public void setSpecies(List<String> species) {
        this.species = species;
    }

    public List<String> getIdentificationCode() {
        return identificationCode;
    }

    public void setIdentificationCode(List<String> identificationCode) {
        this.identificationCode = identificationCode;
    }

}
