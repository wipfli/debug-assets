#!/bin/bash
java -cp planetiler.jar VW.java \
  --output forest.pmtiles \
  --osm_path willisau-latest.osm.pbf \
  --force 2>&1 | tee logs-vw.txt
docker run --rm -it -v "$(pwd)":/data -p 8080:8080 maptiler/tileserver-gl -p 8080
