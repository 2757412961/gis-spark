package edu.zju.gis.hls.trajectory.datastore.util;

import edu.zju.gis.hls.trajectory.analysis.util.FileUtil;
import edu.zju.gis.hls.trajectory.datastore.exception.DataReaderException;
import edu.zju.gis.hls.trajectory.datastore.exception.DataReaderExceptionEnum;
import edu.zju.gis.hls.trajectory.datastore.storage.reader.SourceType;
import lombok.extern.slf4j.Slf4j;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static edu.zju.gis.hls.trajectory.datastore.base.Seperator.SINGLE_LINE;

/**
 * @author Hu
 * @date 2019/8/30
 * 支持从Linux本地文件系统读取Shp文件
 * TODO Attach datasource to support not only sysytem file, but also hdfs file and obs file
 * TODO only support utf8 now, read encode type from cpg file and support different encode type
 **/
@Slf4j
public class ShpDataReader extends DataReader {

  private FeatureIterator<SimpleFeature> reader;

  private ShapefileDataStore shpDataStore;

  public ShpDataReader(String filename){
    this.filename = filename.replace(SourceType.SHP.getPrefix(),"");
  }

  @Override
  public void init(){
    super.init();
    this.readHeader();
    this.readCRS();
    this.initReader();
  }

  private void initReader(){
    File file = new File(filename);
    if(!file.exists()){
      throw new DataReaderException(DataReaderExceptionEnum.FILE_NOT_EXIST, this.filename);
    }
    try {
      this.shpDataStore = new ShapefileDataStore(file.toURI().toURL());
      // 设置编码
      Charset charset = Charset.forName("UTF-8");
      this.shpDataStore.setCharset(charset);
      String typeName = this.shpDataStore.getTypeNames()[0]; // 获取第一层图层名
      SimpleFeatureSource featureSource = this.shpDataStore.getFeatureSource (typeName);
      FeatureCollection<SimpleFeatureType, SimpleFeature> collection = featureSource.getFeatures();
      this.reader = collection.features();
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new DataReaderException(DataReaderExceptionEnum.SYSTEM_READ_ERROR, this.filename);
    } catch (IOException e) {
      e.printStackTrace();
      throw new DataReaderException(DataReaderExceptionEnum.SYSTEM_READ_ERROR, this.filename);
    }
  }

  @Override
  public String next(){
    return reader.hasNext() ? featureToWKT(reader.next(), this.headers):null;
  }

  public SimpleFeature nextFeature() {
    return reader.hasNext() ? reader.next(): null;
  }

  /**
   * 获取文件头信息
   * @return 由header的名字组成的字符串数组
   * @throws Exception
   */
  @Override
  protected String[] readHeader() {
    if(this.headers != null) return this.headers;
    DbaseFileReader dbfReader = null;
    try {
      dbfReader = new DbaseFileReader(new ShpFiles(this.filename), false, Charset.forName("UTF-8"));
      DbaseFileHeader header = dbfReader.getHeader();
      int numFields = header.getNumFields();
      String[] results = new String[numFields];
      for(int i=0; i<numFields; i++){
        results[i] = header.getFieldName(i);
      }
      this.headers = results;
      dbfReader.close();
      return this.headers;
    } catch (IOException e) {
      e.printStackTrace();
      throw new DataReaderException(DataReaderExceptionEnum.SYSTEM_READ_ERROR, this.filename);
    }
  }

  protected String readCRS() {
    if (this.crs == null) {
      String path = filename.replace(".shp", ".prj");
      File file = new File(path);
      if (!file.exists()) return null;
      try {
        this.crs = FileUtil.readByLine(path, 1, false).get(0);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return this.crs;
  }

  @Override
  public void close() throws IOException {
    if(reader != null) reader.close();
    if (this.shpDataStore != null)
      this.shpDataStore.dispose();
  }

  private String featureToWKT(SimpleFeature feature, String[] headers){
    StringBuilder sb = new StringBuilder();
    sb.append(feature.getID().substring(feature.getID().lastIndexOf(".")+1) + "\t");
    for(String header:headers){
      sb.append(String.valueOf(feature.getAttribute(header)) + "\t");
    }
    // 最后一列输出的空间信息的wkt表达形式
    sb.append(feature.getDefaultGeometryProperty().getValue());
    sb.append(SINGLE_LINE);
    return sb.toString();
  }

  public List<String> shpToWKT(){
    ArrayList<String> featureswkt = new ArrayList<String>();
    if(this.reader.hasNext()){
      featureswkt.add(next());
    }
    List<String> wkt = featureswkt;
    return wkt;
  }
}
