package studio.da.gpshelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 * Module : GPSHelper
 *
 * Created by DavidJang on 2017/6/17.
 *
 * 使用说明：
 *
 * 0.将本文件加入工程
 * 1.导入 studio.da.gpshelper 包
 * 2.按照注解说明 调用 Facade Function 区域中的方法
 * 例如 ：GPSHelper.getInstance().RegisterUserGpsLocation(...)
 *
 * 注 位置信息未做持久化 重启场景应用所有位置信息会全部清空
 *
 */

public class GPSHelper
{
    //----------------------region Facade Function------------------------
    //登记用户位置信息 返回操作成功与否
    // (userOpenId为用户唯一标识符，Longtitude为经度，Latitude为纬度 以下不再重复说明)
    public boolean RegisterUserGpsLocation( String userOpenId , double GPSLongtitude , double GPSLatitude ) {
        if(userOpenId != null && (GPSLongtitude <= 180 && GPSLongtitude >= -180) && (GPSLatitude <= 90 && GPSLatitude >= -90))
        {
            GpsPoint _tempLocation = new GpsPoint();
            _tempLocation.longtitude = GPSLongtitude;
            _tempLocation.latitude = GPSLatitude;
            GpsPointSet.put(userOpenId , _tempLocation );
            return true;
        }
        else
        {
            return false;
        }
    }

    //删除已经注册的用户信息 返回操作成功与否
    public boolean UpRegisterUserGpsLocation(String userOpenId) {
        if(GpsPointSet.containsKey((userOpenId)))
        {
            return false;
        }
        else
        {
            GpsPointSet.remove(userOpenId);
            return true;
        }
    }

    //返回指定用户周围指定距离内的用户userOpenId列表
    // （第二个参数是范围 单位是米）
    public ArrayList<String> GetPeopleNearbyByUserId(String userOpenId , float meterRange ) {
        if(!GpsPointSet.containsKey(userOpenId))
        {
            throw new NullPointerException();
        }
        else
        {
            Initialize(meterRange);

            GpsPoint centerPoint = getPointByUserId(userOpenId);

            for(String key : GpsPointSet.keySet())
            {
                GpsPoint gpTemp = (GpsPoint)GpsPointSet.get(key);
                if(gpTemp.latitude > (centerPoint.latitude - latitudeRange) &&
                        gpTemp.latitude < (centerPoint.latitude + latitudeRange))
                {
                    if(gpTemp.longtitude > (centerPoint.longtitude - longtitudeRange) &&
                            gpTemp.longtitude < (centerPoint.longtitude + longtitudeRange))
                    {
                        this.tempUserIdSet.add(key);
                    }
                }
            }
        }
        return  this.tempUserIdSet;
    }

    //返回指定经纬度坐标周围的指定距离内的用户userOpenId列表
    public ArrayList<String> GetPeopleNearbyByGpsLocation(double CenterLongtitude,double CenterLatitude,float meterRange) {
        if((CenterLatitude >= -90) && (CenterLatitude <= 90) && (CenterLongtitude >= -180 && CenterLongtitude <=180))
        {
            Initialize(meterRange);
            for(String key : GpsPointSet.keySet())
            {
                GpsPoint gpTemp = GpsPointSet.get(key);

                if(gpTemp.latitude > (CenterLatitude - latitudeRange) &&
                        gpTemp.latitude < (CenterLatitude + latitudeRange))
                {
                    if(gpTemp.longtitude > (CenterLongtitude - longtitudeRange) &&
                            gpTemp.longtitude < (CenterLongtitude + longtitudeRange))
                    {
                        this.tempUserIdSet.add(key);
                    }
                }
            }
            return  this.tempUserIdSet;
        }
        else
        {
            throw new NullPointerException();
        }
    }

    //根据userOpenId返回两用户距离
    public double GetDistanceByUserId(String userOpenId1 , String userOpenId2) {
        if(GpsPointSet.containsKey(userOpenId1) && userOpenId1 != null && GpsPointSet.containsKey(userOpenId2) && userOpenId2 != null)
        {
            GpsPoint point1 = GpsPointSet.get(userOpenId1);
            GpsPoint point2 = GpsPointSet.get(userOpenId2);
            double DLongtitude = Math.abs( 111 * 1000 * Math.cos(Math.abs(point2.longtitude - point1.longtitude)));
            double DLatitude = 111 * 1000 * Math.abs(point2.latitude - point1.latitude);
            double Distance = Math.sqrt(Math.pow(DLongtitude,2)+Math.pow(DLatitude,2));
            return Distance;
        }
        else
        {
            throw new NullPointerException();
        }
    }

    //根据两个经纬度坐标返回两地之间的距离
    public double GetDistanceByGpsPoint(double Point1Latitude,double Point1Longtitude,double Point2Latitude,double Point2Longtitude) {
        if (((Point1Latitude <= 90) && (Point1Latitude >= -90)) &&
                ((Point2Latitude <= 90) && (Point2Latitude >= -90)) &&
                ((Point1Longtitude <= 180) && (Point1Longtitude >= -180)) &&
                ((Point2Longtitude <= 180) && (Point2Longtitude >= -180))
                )
        {
            double Dlongtitude = Math.abs( 111 * 1000 * Math.cos(Math.abs(Point2Longtitude - Point1Longtitude)));
            double DLatitude = 111 * 1000 * Math.abs(Point2Latitude - Point1Latitude);
            double Distance = Math.sqrt(Math.pow(DLatitude,2)+Math.pow(Dlongtitude,2));
            return Distance;
        }
        else
        {
            throw new NullPointerException();
        }
    }

    //----------------------region Facade Function----------------------


    //---------------------    Internal Stuff    -----------------------

    //   Singleton   -----------------------------------------------
    private static GPSHelper _instance = new GPSHelper();
    public static GPSHelper getInstance()
    {
        return _instance;
    }
    //   Singleton   -----------------------------------------------

    //   Elements   ------------------------------------------------
    protected class GpsPoint {
        public double longtitude;
        public double latitude;
    }
    protected double latitudeRange;
    protected double longtitudeRange;
    protected HashMap<String,GpsPoint> GpsPointSet = new HashMap<String,GpsPoint>();

    protected ArrayList<String> tempUserIdSet = new ArrayList<String>();                 //tempVar
    //   Elements   ------------------------------------------------

    //   Internal Function   ---------------------------------------
    protected void Initialize(float meter) {
        this.tempUserIdSet.clear();
        this.longtitudeRange = Math.abs((double)(meter/111)/1000);
        this.latitudeRange = Math.abs((double)Math.acos((meter /111)/1000));
    }
    protected GpsPoint getPointByUserId(String userOpenId) {
        return this.GpsPointSet.get(userOpenId);
    }
    //   Internal Function   ---------------------------------------
}
//----------------------    Internal Stuff    ------------------------