package br.com.webnow.domain;

import java.io.Serializable;
import java.util.Locale;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.support.index.IndexType;

/**
 * Entidade Partida do Itinerario ( Projeto Web )
 * @author wellington
 *
 */
@NodeEntity
public class Partida implements Serializable{
	
		
	private static final long serialVersionUID = -7444625527145174350L;
	@GraphId
	private Long nodeId;
	private String enderecoPartida;
	private Double latitude;
	private Double longitude;
	
	private void updateWkt(){
		Locale enLocale = new Locale("en", "EN");
	    this.wkt = String.format(enLocale, "POINT( %.2f %.2f )", this.getLongitude(), this.getLatitude());
	}
	
	@Indexed(indexName="localPartida", indexType=IndexType.POINT)
	private String wkt;
	
		
	public Long getNodeId() {
		return nodeId;
	}
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}
	public String getWkt() {
		return wkt;
	}
	public void setWkt(String wkt) {
		this.wkt = wkt;
	}
	public String getEnderecoPartida() {
		return enderecoPartida;
	}
	public void setEnderecoPartida(String enderecoPartida) {
		this.enderecoPartida = enderecoPartida;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
		this.updateWkt();
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
		this.updateWkt();
	}
	
	

}
