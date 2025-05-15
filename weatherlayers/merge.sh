rm U_swiss_fixed.tif
rm V_swiss_fixed.tif
rm magnitude_swiss_fixed.tif

gdalwarp -overwrite -tr 0.013158586806079 -0.013377209829348 U_swiss.tif U_swiss_fixed.tif
gdalwarp -overwrite -tr 0.013158586806079 -0.013377209829348 V_swiss.tif V_swiss_fixed.tif

gdal_calc.py --calc='sqrt(A * A + B * B)' -A U_swiss_fixed.tif --A_band=1 -B V_swiss_fixed.tif --B_band=1 --outfile wind_magnitude_fixed.tif

gdal_calc.py --calc='(-1) * A' -A U_swiss_fixed.tif --A_band=1 --outfile U_swiss_fixed_inv.tif
gdal_calc.py --calc='(-1) * A' -A V_swiss_fixed.tif --A_band=1 --outfile V_swiss_fixed_inv.tif

gdalbuildvrt -separate wind.vrt wind_magnitude_fixed.tif U_swiss_fixed_inv.tif V_swiss_fixed.tif

# gdal_translate -ot Byte -scale -128 127 0 255 wind.vrt wind.png
gdal_translate -ot Byte -scale 128 -127 0 255 wind.vrt wind.png
