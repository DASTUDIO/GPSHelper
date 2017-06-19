<?php 
error_reporting(E_ALL^E_NOTICE);
if(
isset($_GET["x1"])&&
isset($_GET["x2"])&&
isset($_GET["y1"])&&
isset($_GET["y2"])
)
{
$longtitude1 = doubleval($_GET["x1"]);
$latitude1 = doubleval($_GET["y1"]);
$longtitude2 = doubleval($_GET["x2"]);
$latitude2 = doubleval($_GET["y2"]);
$DLongtitude = cos(abs($latitude2 - $latitude1))*111*1000;
$DLatitude = abs($longtitude2 - $longtitude2)*111*1000;
$RealDistance = sqrt(pow($DLongtitude,2)+pow($DLatitude,2));
echo $RealDistance;
}
else
{
	echo '<center>';
	echo '<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />';
	echo iconv("GB2312","UTF-8",'中文');
	echo "<h1>格式错误!</h1> ";
	echo "应这样请求:</br></br>";
	echo "<b>http://hostname/GPS.php?x1=地点1的经度&y1=地点1的纬度&x2=地点2的经度&y2=地点2的纬度</b></br></br>";
	echo "结果返回两地点的距离 单位是米(m)</br></br>";
	echo "例如 ：<i>http://192.168.0.9/GPS.php?x1=39.26&y1=41.03&x2=121.48&y2=31.22</i></br></br>";
	echo '</center>';
}
?>