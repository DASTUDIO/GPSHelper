using System;
using System.Collections.Generic;

/// <summary>
/// Created by DavidJang 2017/6/17
/// 使用说明
/// 1.using DA.STUDIO;
/// 2.参照注解说明 调用Facade Function 区域中的方法
/// 例如: GpsLocationHelper.getInstance().RegisterUserGpsLocation(...)
/// </summary>

namespace DA.STUDIO
{
    public class GpsLocationHelper
    {
        #region Facade Functions

        //登记用户位置信息 返回操作成功与否 (userOpenId为用户唯一标识符，Longtitude为经度，Latitude为纬度 以下不再重复说明)
        public bool RegisterUserGpsLocation(string userOpenId, double GPSLongtitude, double GPSLatitude)
        {
            if (userOpenId != null &&
                (GPSLongtitude <= 180 && GPSLongtitude >= -180) &&
                (GPSLatitude <= 90 && GPSLatitude >= -90))
            {
                GpsPoint _tempLocation =
                    new GpsPoint() { longtitude = GPSLongtitude, latitude = GPSLatitude };

                if (GpsPointSet.ContainsKey(userOpenId))
                {
                    GpsPointSet[userOpenId] = _tempLocation;
                }
                else
                {
                    GpsPointSet.Add(userOpenId, _tempLocation);
                }
                return true;
            }
            else
            {
                //没写 userOpenId 或者 经纬度不合法
                return false;
            }
        }

        //删除已经注册的用户信息 返回操作成功与否
        public bool UnRegisterUserGpsLocation(string userOpenId)
        {
            if (!GpsPointSet.ContainsKey(userOpenId))
            {
                //未登记该角色
                return false;
            }
            else
            {
                GpsPointSet.Remove(userOpenId);
                return true;
            }
        }

        //返回指定用户周围指定距离内的用户userOpenId列表 （第二个参数是范围 单位是米）
        public List<string> GetPeopleNearbyByUserId(string userOpenId, float meterRange)
        {
            if (!GpsPointSet.ContainsKey(userOpenId))
            {
                throw new Exception("该账号未注册位置！");
            }
            else
            {
                Initialize(meterRange);

                GpsPoint centerPotint = getPointByUserId(userOpenId);

                foreach (var gp in GpsPointSet)
                {
                    GpsPoint gpTemp = (GpsPoint)gp.Value;
                    if (gpTemp.latitude > (centerPotint.latitude - latitudeRange) &&
                        gpTemp.latitude < (centerPotint.latitude + latitudeRange))
                    {
                        if (gpTemp.longtitude > (centerPotint.longtitude - longtitudeRange) &&
                            gpTemp.longtitude < (centerPotint.longtitude + longtitudeRange))
                        {
                            this.tempUserIdSet.Add(gp.Key);
                        }
                    }
                }
            }
            return this.tempUserIdSet;
        }

        //返回指定经纬度坐标周围的指定距离内的用户userOpenId列表
        public List<string> GetPeopleNearbyByGpsLocation(double CenterLongtitude, double CenterLatitude, float meterRange)
        {
            if ((CenterLatitude >= -90) && (CenterLatitude <= 90) &&
                (CenterLongtitude >= -180 && CenterLongtitude <= 180)
                )
            {
                Initialize(meterRange);

                foreach (var gp in GpsPointSet)
                {

                    GpsPoint gpTemp = gp.Value;

                    if (gpTemp.latitude > (CenterLatitude - latitudeRange) &&
                        gpTemp.latitude < (CenterLatitude + latitudeRange))
                    {
                        if (gpTemp.longtitude > (CenterLongtitude - longtitudeRange) &&
                            gpTemp.longtitude < (CenterLongtitude + longtitudeRange))
                        {
                            this.tempUserIdSet.Add(gp.Key);
                        }
                    }
                }
                return this.tempUserIdSet;
            }
            else
            {
                throw new Exception("GPS坐标错误");
            }
        }

        //根据userOpenId返回两用户距离
        public double GetDistanceByUserId(string userOpenId1, string userOpenId2)
        {
            if (GpsPointSet.ContainsKey(userOpenId1) && userOpenId1 != null)
            {
                if (GpsPointSet.ContainsKey(userOpenId2) && userOpenId2 != null)
                {
                    GpsPoint point1 = GpsPointSet[userOpenId1];
                    GpsPoint point2 = GpsPointSet[userOpenId2];

                    double DLongtitude = Math.Abs (111 * 1000 *
                        Math.Cos(
                            Math.Abs( point2.longtitude - point1.longtitude )));

                    double DLatitude = 111 * 1000 *
                        Math.Abs(
                            point2.latitude - point1.latitude
                            );

                    double Distance =
                        Math.Sqrt(
                            Math.Pow(DLongtitude, 2) + Math.Pow(DLatitude, 2)
                        );

                    return Distance;
                }
                else
                {
                    throw new Exception("userOpenId2 not exist!");
                }
            }
            else
            {
                throw new Exception("userOpenId1 not exist!");
            }
        }

        //返回两经纬度坐标之间的距离
        public double GetDistanceByGpsPoints(double Point1Latitude, double Point1Longtitude, double Point2Latitude, double Point2Longtitude)
        {

            if (((Point1Latitude <= 90) && (Point1Latitude >= -90)) &&
                ((Point1Longtitude <= 180) && (Point1Longtitude >= -180)) &&
                ((Point2Latitude <= 90) && (Point2Latitude >= -90)) &&
                ((Point2Longtitude <= 180) && (Point2Longtitude >= -180))
                )
            {
                double DLongtitude = Math.Abs( 111 * 1000 *
                    Math.Cos(
                        Math.Abs(Point2Longtitude - Point1Latitude)));

                double DLatitude = 111 * 1000 *
                    Math.Abs(
                        Point2Latitude - Point1Latitude
                        );

                double Distance =
                    Math.Sqrt(
                        Math.Pow(DLongtitude, 2) + Math.Pow(DLatitude, 2)
                        );

                return Distance;
            }
            else
            {
                throw new Exception("GPS坐标错误");
            }
        }

        #endregion

        #region Internal Stuff

        #region Elements
        
        protected double longtitudeRange;

        protected double latitudeRange;

        protected class GpsPoint
        {
            //y 纬度
            public double longtitude;
            //x 经度
            public double latitude;
        }

        protected Dictionary<string, GpsPoint> GpsPointSet
            = new Dictionary<string, GpsPoint>();

        protected List<string> tempUserIdSet = new List<string>();

        #endregion

        #region SingleTon 

        private static GpsLocationHelper _instance = new GpsLocationHelper();

        public static GpsLocationHelper getInstance()
        {
            return _instance;
        }

        #endregion

        #region InternalFunction

        protected void Initialize(float meter)
        {
            this.tempUserIdSet.Clear();
            this.longtitudeRange = Math.Abs((double)(meter / 111) / 1000);
            this.latitudeRange = Math.Abs((double)Math.Acos(( meter / 111 ) / 1000));
        }

        protected GpsPoint getPointByUserId(string userOpenId)
        {
            return this.GpsPointSet[userOpenId];
        }

        #endregion

        #endregion

    }
}
