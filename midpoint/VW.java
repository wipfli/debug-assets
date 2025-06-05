import java.nio.file.Path;
import java.util.List;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.Planetiler;
import com.onthegomap.planetiler.Profile;
import com.onthegomap.planetiler.config.Arguments;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.util.ZoomFunction;
import com.onthegomap.planetiler.geo.VWSimplifier;
import com.onthegomap.planetiler.geo.MidpointSmoother;
import com.onthegomap.planetiler.stats.Stats;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.FeatureMerge;


public class VW implements Profile {

  public static void main(String[] args) throws Exception {
    var arguments = Arguments.fromArgsOrConfigFile(args).withDefault("download", true);
    String area = arguments.getString("area", "geofabrik area to download", "monaco");
    Planetiler.create(arguments)
        .setProfile(new VW())
        // override this default with --osm-path="path/to/data.osm.pbf"
        .addOsmSource("osm", Path.of("data", area + ".osm.pbf"), "geofabrik:" + area)
        // override this default with --output="path/to/output.pmtiles"
        .overwriteOutput(Path.of("data", "toilets.pmtiles"))
        .run();
  }

  @Override
  public void processFeature(SourceFeature sourceFeature, FeatureCollector features) {
    if (sourceFeature.canBePolygon() && sourceFeature.hasTag("landuse", "forest")) {
      features.polygon("forest");
    }
  }

  @Override
  public List<VectorTile.Feature> postProcessLayerFeatures(String layer, int zoom,
  List<VectorTile.Feature> items) throws GeometryException {
    var simplifier = new VWSimplifier().setTolerance(15 * 0.0625);
    var smoother = new MidpointSmoother();
    return FeatureMerge.mergeNearbyPolygons(items, 3.125, 3.125, 0.5, 0.5, Stats.inMemory(), simplifier.andThen(smoother));
  }

  @Override
  public boolean isOverlay() {
    return true;
  }

  @Override
  public String attribution() {
    return OSM_ATTRIBUTION;
  }
}
