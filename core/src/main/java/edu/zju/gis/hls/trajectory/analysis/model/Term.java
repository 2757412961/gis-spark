package edu.zju.gis.hls.trajectory.analysis.model;

import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.Serializable;

import static edu.zju.gis.hls.trajectory.analysis.model.FieldType.*;

/**
 * @author Hu
 * @date 2019/12/16
 **/
public class Term implements Serializable {

  public static CoordinateReferenceSystem DEFAULT_CRS = getDefaultCrs();
  public static int QUADTREE_MIN_Z = 4;
  public static int QUADTREE_MAX_Z = 16;
  public static int QUADTREE_DEFAULT_LEVEL = 10;
  public static int SCREEN_TILE_SIZE = 256;

  public static Integer FIELD_NOT_EXIST = -99;
  public static Integer FIELD_LAST = -1;
  public static Integer FIELD_EXIST = 99;
  public static Integer FIELD_LENGTH = 255;

  public static Field FIELD_DEFAULT_SHAPE =  new Field(SHAPE_FIELD.name(), SHAPE_FIELD.name().toLowerCase(), Geometry.class.getName(), 0, FIELD_LAST, SHAPE_FIELD);
  public static Field FIELD_DEFAULT_ID = new Field(ID_FIELD.name(), ID_FIELD.name().toLowerCase(), String.class.getName(), 0, FIELD_NOT_EXIST, ID_FIELD);
  public static Field FIELD_DEFAULT_TIME = new Field(TIME_FIELD.name(), TIME_FIELD.name().toLowerCase(), Long.class.getName(), 0, FIELD_NOT_EXIST, TIME_FIELD);
  public static Field FIELD_DEFAULT_START_TIME = new Field(START_TIME_FIELD.name(), START_TIME_FIELD.name().toLowerCase(), Long.class.getName(), 0, FIELD_NOT_EXIST, START_TIME_FIELD);
  public static Field FIELD_DEFAULT_END_TIME = new Field(END_TIME_FIELD.name(), END_TIME_FIELD.name().toLowerCase(), Long.class.getName(), 0, FIELD_NOT_EXIST, END_TIME_FIELD);

  // Geometry 转 GeoJSON 的坐标精度
  public static Integer GEOMETRY_JSON_DECIMAL = 9;

  public static String WKT_4528 = "PROJCS[\"CGCS2000 / 3-degree Gauss-Kruger zone 40\",GEOGCS[\"China Geodetic Coordinate System 2000\",DATUM[\"China_2000\",SPHEROID[\"CGCS2000\",6378137,298.257222101,AUTHORITY[\"EPSG\",\"1024\"]],AUTHORITY[\"EPSG\",\"1043\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4490\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",120],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",40500000],PARAMETER[\"false_northing\",0],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AUTHORITY[\"EPSG\",\"4528\"]]";

  public static CoordinateReferenceSystem getDefaultCrs() {
    try {
      return CRS.decode("epsg:4326");
    } catch (FactoryException e) {
      e.printStackTrace();
    }
    return null;
  }

}