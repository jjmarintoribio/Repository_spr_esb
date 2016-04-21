package ib.visa.esb.bth.gen;

import java.io.File;
import java.io.FilenameFilter;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class MF_ESB_BTH_CTLARCHIVO_MoverArchivo extends MbJavaComputeNode 
{
	private static final String ATTRIBUTE_SOURCE_DIRECTORY = "source.directory";
	private static final String ATTRIBUTE_TARGET_DIRECTORY = "target.directory";
	private static final String ATTRIBUTE_ARCHIVE_DIRECTORY = "archive.directory";
	
	private static final String FILE_EXTENSION_TMP = ".TMP";
	private static final String FILE_EXTENSION_DAT = ".DAT";
	private static final String FILE_NAME_PATTERN = "RECUVIDAT";
	
	/* (non-Javadoc)
	 * @see com.ibm.broker.javacompute.MbJavaComputeNode#evaluate(com.ibm.broker.plugin.MbMessageAssembly)
	 */
	public void evaluate(MbMessageAssembly inAssembly) throws MbException 
	{
		MbOutputTerminal out = getOutputTerminal("out");
		
		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		
		try
		{
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);

			// Se leen los archivos del directorio origen
			File sourceDirectory = new File((String) getUserDefinedAttribute(ATTRIBUTE_SOURCE_DIRECTORY));
			File[] sourceFiles = sourceDirectory.listFiles(new FilenameFilter()
			{
				@Override
				public boolean accept(File directory, String name)
				{
					return name.contains(FILE_NAME_PATTERN);
				}
			});
			
			// Se realiza la copia de archivos hacia el directorio de trabajo
			for (final File sourceFile : sourceFiles)
			{
				// Se verifica si el archivo ya ha sido procesado buscandolo en el directorio mqsiarchive
				File archiveDirectory = new File((String) getUserDefinedAttribute(ATTRIBUTE_ARCHIVE_DIRECTORY));
				File[] archiveFiles = archiveDirectory.listFiles(new FilenameFilter()
				{
					@Override
					public boolean accept(File directory, String name)
					{
						return name.equals(sourceFile.getName());
					}
				});
				
				if(archiveFiles.length == 0)
				{
					// Se copia el archivo a la ruta de trabajo bajo la extension .TMP
					String targetDirectory = (String) getUserDefinedAttribute(ATTRIBUTE_TARGET_DIRECTORY);
					String targetFileName = sourceFile.getName().substring(0, sourceFile.getName().indexOf("."));
					
					File targetFile = new File(targetDirectory.concat(targetFileName).concat(FILE_EXTENSION_TMP));
					
					//if(sourceFile.isFile() && sourceFile.canRead()){
					boolean success = sourceFile.renameTo(targetFile);
						
					if(success){
					// Una vez finalizada la copia se renombra el archivo a la extension .DAT para ser leido por el flujo.
						if(targetFile.exists()){
							targetFile.renameTo(new File(targetDirectory.concat(targetFileName).concat(FILE_EXTENSION_DAT)));	
						}
						//boolean del = sourceFile.renameTo(new File(targetDirectory.concat(sourceFile.getName())));
						//if(!del){
						//}
					}
				}
				else
				{
					// Se elimina el archivo dado que ya se proceso.
					sourceFile.delete();
					System.out.println("Ya se ha procesado el archivo '" + sourceFile.getName() + "'.");	
					//boolean del = sourceFile.delete();
					//if(!del){
						//System.out.println("Ya se ha procesado el archivo '" + sourceFile.getName() + "'.");
					//}
				}
			}
		}
		catch (MbException e) 
		{
			throw e;
		} 
		catch (RuntimeException e) 
		{
			throw e;
		} 
		catch (Exception e) 
		{
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		}
		
		out.propagate(outAssembly);
	}
}