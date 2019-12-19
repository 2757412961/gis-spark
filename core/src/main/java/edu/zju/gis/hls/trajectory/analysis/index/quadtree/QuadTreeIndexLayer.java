package edu.zju.gis.hls.trajectory.analysis.index.quadtree;

import edu.zju.gis.hls.trajectory.analysis.index.IndexType;
import edu.zju.gis.hls.trajectory.analysis.model.Feature;
import edu.zju.gis.hls.trajectory.analysis.rddLayer.IndexedLayer;
import edu.zju.gis.hls.trajectory.analysis.rddLayer.Layer;
import lombok.Getter;
import lombok.Setter;
import org.apache.spark.api.java.function.Function;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hu
 * @date 2019/12/18
 * 四叉树索引的 RDD Layer
 **/
@Getter
@Setter
public class QuadTreeIndexLayer<L extends Layer> extends IndexedLayer<L> {

  private static final Logger logger = LoggerFactory.getLogger(QuadTreeIndexLayer.class);

  private PyramidConfig pc;
  private int qz;

  public QuadTreeIndexLayer(PyramidConfig pc, int qz) {
    this.indexType = IndexType.QUADTREE;
    this.pc = pc;
    this.qz = qz;
  }

  @Override
  public QuadTreeIndexLayer query(Geometry geometry) {

    ReferencedEnvelope envelope = JTS.toEnvelope(geometry);
    ZLevelInfo tZLevelInfo = TileUtil.initZLevelInfoPZ(pc, envelope)[qz - pc.getZLevelRange()[0]];
    List<String> tiles = new ArrayList<>();
    for (int tile_x = tZLevelInfo.getTileRanges()[0]; tile_x <= tZLevelInfo.getTileRanges()[1]; tile_x++) {
      for (int tile_y = tZLevelInfo.getTileRanges()[2]; tile_y <= tZLevelInfo.getTileRanges()[3]; tile_y++) {
        tiles.add((new TileID(qz, tile_x, tile_y)).toString());
      }
    }

    int partitionNum = this.layer.getNumPartitions();

    this.layer = (L) this.layer.filterToLayer(new Function<Tuple2, Boolean>() {
      @Override
      public Boolean call(Tuple2 in) throws Exception {
        Object k = in._1;
        if (k instanceof String) {
          return tiles.contains(k);
        }
        return false;
      }
    }).repartitionToLayer(partitionNum).filterToLayer(new Function<Tuple2, Boolean>() {
      @Override
      public Boolean call(Tuple2 in) throws Exception {
        Feature f = (Feature) in._2;
        return f.getGeometry().intersects(geometry);
      }
    });

    return this;
  }

}
