package ib.visa.esb.bth.acttar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MF_SB_BTH_RCVARCHIVO_PrepararArchivo {

	public static String unzipFile(String fileIn, String pathFileOut, String deleteFile, String msjError ) {

		FileInputStream fis = null;
		ZipInputStream zipIs = null;
		ZipEntry zEntry = null;
		String opFilePath = null;
		String fileZip = "";
		try {
			fis = new FileInputStream(fileIn);
			zipIs = new ZipInputStream(new BufferedInputStream(fis));

			while ((zEntry = zipIs.getNextEntry()) != null) {
				try {
					byte[] tmp = new byte[4 * 1024];
					FileOutputStream fos = null;
					fileZip = zEntry.getName();
					opFilePath = pathFileOut + zEntry.getName();
					fos = new FileOutputStream(opFilePath);
					int size = 0;
					while ((size = zipIs.read(tmp)) != -1) {
						fos.write(tmp, 0, size);
					}
					fos.flush();
					fos.close();
				} catch (Exception ex) {

				}
			}
			
			zipIs.close();
			//Borrado del archivo
			if (deleteFile.equals("S")){
			   File fileZipped = new File(fileIn);
			   fileZipped.delete();
			   
			}			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			msjError = e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			msjError = e.getMessage();
		}
		return fileZip;

	}

}
