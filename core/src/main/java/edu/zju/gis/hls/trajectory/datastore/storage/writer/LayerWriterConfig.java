package edu.zju.gis.hls.trajectory.datastore.storage.writer;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Hu
 * @date 2019/12/19
 **/
@Getter
@Setter
public abstract class LayerWriterConfig implements Serializable {
  protected String sinkPath;
}