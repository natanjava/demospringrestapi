package demo.api.rest.service;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;



@Service
public class ServiceRelatorio implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public byte[] geraRelatorio (String nomeRelatorio , Map<String,Object> params, ServletContext servletContext ) throws Exception  {
		
		/* Obtem conexão com BD*/
		Connection connection= jdbcTemplate.getDataSource().getConnection();
		
		/* Carregar o caminho do arquivo jasper */
		String caminhoJasper = servletContext.getRealPath("relatorios")
				+ File.separator + nomeRelatorio + ".jasper";
		
		/* Gerar o relatório com o caminho do relatorio, parametros (lista vazia de hashMap) e conexão*/
		JasperPrint print = JasperFillManager.fillReport(caminhoJasper, params, connection);
		
		/*Exporta para byte o PDF para disponibilizar o download*/
		byte [] finalReport = JasperExportManager.exportReportToPdf(print);
		
		/* Fecha a conexão*/		
		connection.close();
		
		return finalReport; 
	}

}
