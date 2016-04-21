package ib.visa.esb.bth.gen;
/**
 * @author Ruben Mejia
 */
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchProviderException;

import org.bouncycastle.openpgp.PGPException;

import pe.com.visanet.spr.crypto.encryptor.GeneralEncryptor;
import pe.com.visanet.spr.crypto.encryptor.support.KeyStore;
import pe.com.visanet.spr.crypto.hsm.HsmProxy;


public class Seguridad { 



    private static HsmProxy hsm;

    private static KeyStore ks;  

    private static GeneralEncryptor ge;  

    private static final String C_TIPO_LLAVE_PUBLICA = "5";

    private static final String C_TIPO_LLAVE_PGP_PRIVADA = "6";

    private static final String C_TIPO_LLAVE_PGP_PUBLICA = "8";

    private static final String C_TIPO_LLAVE_PUBLICA_ALIGNET = "9";

    private static final String C_SUFIJO_ARCHIVO_TEMPORAL = "_TEMP";

    

    private static final String M_TIPO_LLAVE_PRIVADA = "10";

    private static final String M_TIPO_LLAVE_PUBLICA = "12";

    

   



    /**

     * 

     * @param strHost

     * @param intPort

     * @param strLmkId

     */

    public static void iniciarProxy(String strHost, String intPort, String strLmkId) {

        hsm = new HsmProxy(strHost, Integer.parseInt(intPort) , strLmkId);

        ks = new KeyStore();

    }



    /**

     * 

     * @param strLlaveMaestra

     * @param strLlaveHexEncriptada

     * @param strCodEmisor

     * @param strTipo

     */

    public static void desencriptarLlave(String strLlaveMaestra,String strLlaveHexEncriptada,String strCodEmisor, String  strTipo) {

        

        String strLlaveHex = null;

        int intTipo = Integer.parseInt(strTipo);



        if (!strTipo.equals(C_TIPO_LLAVE_PUBLICA) 

                && !strTipo.equals(C_TIPO_LLAVE_PGP_PRIVADA) 

                && !strTipo.equals(C_TIPO_LLAVE_PGP_PUBLICA)

                && !strTipo.equals(C_TIPO_LLAVE_PUBLICA_ALIGNET)

                && !strTipo.equals(M_TIPO_LLAVE_PRIVADA)

                && !strTipo.equals(M_TIPO_LLAVE_PUBLICA)) {

            strLlaveHex = hsm.desencriptar(strLlaveMaestra,strLlaveHexEncriptada);    

        }else{

            strLlaveHex = strLlaveHexEncriptada;

        }

        

        switch (intTipo){

            case 1:ks.guardarLlaveNumeroTarjeta(strLlaveHex);

            case 2:ks.guardarLlaveSal(strLlaveHex);

            case 3:ks.guardarLlaveArchivoTarjetas(strCodEmisor,strLlaveHex);

            case 4:ks.guardarLlaveTrama(strCodEmisor,strLlaveHex);

            case 5:ks.guardarLlaveFirmaDigital(strCodEmisor,strLlaveHex);

            case 6:ks.guardarLlavePrivadaArchivoTarjetasPGP(strLlaveHex);

            case 7:ks.guardarPassphrase(strLlaveHex);

            case 8:ks.guardarLlaveArchivoTarjetasPGP(strLlaveHex);

            case 10:ks.guardarLlavePrivadaMigracionPGP(strLlaveHex);

            case 11:ks.guardarPassphraseMigracion(strLlaveHex);

            
        }

        

    }



    /**

     *     

     */

    public static void iniciarEncriptador() {

        ge = new GeneralEncryptor(ks);

        

    }



    /**

     * 

     * @param strNroTarjeta

     * @return

     */

    public static String encriptarNroTarjeta(String strNroTarjeta) {

        return ge.encriptarNumeroTarjeta(strNroTarjeta);

        

    }

    

    /**

     * 

     * @param strNroTarjetaEncriptado

     * @return

     */

    public static String desencriptarNroTarjeta(String strNroTarjetaEncriptado) {

        return ge.desencriptarNumeroTarjeta(strNroTarjetaEncriptado);

        

    }

    

    

    /**

     * 

     * @param strSal

     * @return

     */

    public static String encriptarSal(String strSal) {

        return ge.encriptarSal(strSal);

        

    }

    

    /**

     * 

     * @param strSalEncriptado

     * @return

     */

    public static String desencriptarSal(String strSalEncriptado) {

        return ge.desencriptarSal(strSalEncriptado);

        

    }

    



    /**

     * 

     * @param strCodEmisor

     * @param strPathFileClaro

     * @param strNombreFileClaro 

     * @param strPathFileEncriptado

     * @param strNombreFileEncriptado

     */

    public static void encriptarArchivoTarjetas(String strCodEmisor, String strPathFileClaro, String strNombreFileClaro) {

        File filClaro = new File(strPathFileClaro + strNombreFileClaro);

        File filEncriptado = new File(strPathFileClaro + strNombreFileClaro.replaceAll(C_SUFIJO_ARCHIVO_TEMPORAL, ""));    

        ge.encriptarArchivoTarjetas(strCodEmisor, filClaro, filEncriptado);

        filClaro.delete();

    }

 

    /**

     * 

     * @param strPathFileClaro

     * @param strNombreFileClaro 

     * @param strPathFileEncriptado

     * @param strNombreFileEncriptado

     * @throws PGPException 

     * @throws NoSuchProviderException 

     */

    public static void encriptarArchivoTarjetasPgp( String strPathFileClaro, String strNombreFileClaro) {

        File filClaro = new File(strPathFileClaro + strNombreFileClaro);

        File filEncriptado = new File(strPathFileClaro + strNombreFileClaro.replaceAll(C_SUFIJO_ARCHIVO_TEMPORAL, ""));

        try {

            ge.encriptarArchivoTarjetasPGP(filClaro, filEncriptado);

        } catch (NoSuchProviderException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        } catch (PGPException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }        

        filClaro.delete();

    }

    

/**

 * 

 * @param strCodEmisor

 * @param strPathFileEncriptado

 * @param strNombreFileEncriptado

 * @param strDirectorioArchive

 * @param bolFileEncriptado: Indicador para saber si el file esta encriptado y se necesita desencriptarlo

 */

    public static void desencriptarArchivoTarjetas(String strCodEmisor, String strPathFileEncriptado, String strNombreFileEncriptado, String strPathFileDestino,String strDirectorioArchive, String strFileEncriptado) {

        File filOrigen = new File(strPathFileEncriptado + strDirectorioArchive + strNombreFileEncriptado);

        File filDesencriptado = new File(strPathFileDestino + strNombreFileEncriptado);

        if (strFileEncriptado.equals("1")){    

            ge.desencriptarArchivoTarjetas(strCodEmisor, filOrigen, filDesencriptado);

        }else{

            filOrigen.renameTo(filDesencriptado);

        }

        filOrigen.delete();

    

    }

    

    /**

     * 

     * @param strPathFileEncriptado

     * @param strNombreFileEncriptado

     * @param strDirectorioArchive

     * @param bolFileEncriptado: Indicador para saber si el file esta encriptado y se necesita desencriptarlo

     */

    public static void desencriptarArchivoTarjetasPgp(String strPathFileEncriptado, String strNombreFileEncriptado, String strPathFileDestino,String strDirectorioArchive) {

        File filOrigen = new File(strPathFileEncriptado + strDirectorioArchive + strNombreFileEncriptado);

        File filDesencriptadoTmp =  new File(strPathFileDestino + strNombreFileEncriptado+ ".tmp");

        ge.desencriptarArchivoTarjetasPGP(filOrigen, filDesencriptadoTmp);

        filOrigen.delete();

        File filDesencriptado = new File(strPathFileDestino + strNombreFileEncriptado);

        filDesencriptadoTmp.renameTo(filDesencriptado);

        filDesencriptadoTmp.delete();        

    }     

    

    



    /**

     * 

     * @param strPathFileEncriptado

     * @param strNombreFileEncriptado

     * @param strDirectorioArchive

     * @param bolFileEncriptado: Indicador para saber si el file esta encriptado y se necesita desencriptarlo
     * @throws IOException 

     */

    public static void desencriptarArchivoMigracionPgp(String strPathFileEncriptado, String strNombreFileEncriptado, String strPathFileDestino,String strDirectorioArchive) {

        File filOrigen = new File(strPathFileEncriptado + strDirectorioArchive + strNombreFileEncriptado);
        

                    

        File filDesencriptadoTmp =  new File(strPathFileDestino + strNombreFileEncriptado.replace("pgp", "csv") + ".tmp");        

        ge.desencriptarArchivosMigracionPGP(filOrigen, filDesencriptadoTmp);
        
      filOrigen.delete(); 

        File filDesencriptado = new File(strPathFileDestino + strNombreFileEncriptado.replace("pgp", "csv"));

        filDesencriptadoTmp.renameTo(filDesencriptado);

        filDesencriptadoTmp.delete();           

    }     

    

    

    /** 

     * 

     * @param strSal

     * @return 

     */

    public static String validarFirmaDigital(String strCodEmisor,String strTrama, String strFirmaDigital) {

        String resultado = ge.validarFirmaDigital(strCodEmisor, strTrama, strFirmaDigital);



         return resultado;

    }

    

    

}