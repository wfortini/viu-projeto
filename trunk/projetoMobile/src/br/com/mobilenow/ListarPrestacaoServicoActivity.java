package br.com.mobilenow;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import br.com.mobilenow.adapter.PrestarServicoListAdapter;
import br.com.mobilenow.util.Info;
import br.com.mobilenow.util.ServiceBoxMobileUtil;
import br.com.servicebox.android.common.activity.CommonActivity;
import br.com.servicebox.android.common.fragment.CommonFragment;
import br.com.servicebox.android.common.util.CommonUtils;
import br.com.servicebox.android.common.util.GuiUtils;
import br.com.servicebox.common.net.ListaServicoResponse;
import br.com.servicebox.common.net.Response;

public class ListarPrestacaoServicoActivity extends CommonActivity {
	
	public static final String TAG = ListarPrestacaoServicoActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null)
	    {
	        getSupportFragmentManager().beginTransaction()
	                .replace(android.R.id.content, new ListarPrestacaoServicoFragment())
	                .commit();
	    }
	}
	
	
	public static class ListarPrestacaoServicoFragment extends CommonFragment implements
        OnItemClickListener {

		private PrestarServicoListAdapter mAdapter;		
		private ProgressDialog progressDialog;
		private List<Info> prestarLista = new ArrayList<Info>();
		private ListView listaPrestacaoServico;
		
		@Override
		public View onCreateView(org.holoeverywhere.LayoutInflater inflater,
				ViewGroup container, Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
		  View v = inflater.inflate(R.layout.activity_listar_prestacao_servico, container, false);
		  return v;
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
		   super.onViewCreated(view, savedInstanceState);
		   init(view, savedInstanceState);
		  
		}
		
		void init(View v, Bundle savedInstanceState){			
			
			listaPrestacaoServico = (ListView) v.findViewById(R.id.list);            
			listaPrestacaoServico.setOnItemClickListener(this);            
		   new RequisicaoTask().execute();      
		 
		} // fim metodo init      
		  
		
		/** processamento assincrono **/	
		private class RequisicaoTask extends AsyncTask<Void, Void, Response>{
					
				Response response = null;
				String url;
				
				@Override
		      protected void onPreExecute() {
					super.onPreExecute();
					progressDialog = new ProgressDialog(getActivity());
					progressDialog.setTitle(R.string.aguarde_por_favor);
					progressDialog.setMessage(CommonUtils.getStringResource(R.string.processando));
					progressDialog.setCancelable(false);
					progressDialog.show();
				}
		
				@Override
				protected Response doInBackground(Void... params) {					
					
					try {
						url = getString(R.string.ip_servidor_servicebox)
				 				   .concat(":8080/projetoWeb/listarPrestarServicoOferecidos.json");  
						
						RestTemplate restTemplate = ServiceBoxMobileUtil.getRestTemplate();
						
						MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		               
						 map.add("idUsuario", ServiceBoxApplication.getUsuario().getNodeId().toString());						 			
			             
			             HttpHeaders headers = new HttpHeaders();
			             headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		
			             HttpEntity<MultiValueMap<String, Object>> entity = 
			            		     new HttpEntity<MultiValueMap<String, Object>>(map, headers);
		
						
			             Response response = null;
			             if (GuiUtils.checkOnline()){
						         response = restTemplate.postForObject(url, entity, ListaServicoResponse.class);
			             }
			             
			             return response;
						
					}catch(ResourceAccessException rae){
						CommonUtils.error(TAG, rae.getMessage());
						response = new Response(false, "Falha na localiza��o das Presta��es de servi�o \n Servidor n�o responde.", null, Response.ERRO_DESCONHECIDO);
						progressDialog.dismiss();
					} catch (Exception e) {
						Log.e(TAG, e.getMessage());
						response = new Response(false, "Falha na localiza��o das Presta��es de servi�o, tente novamente mais tarde.", null, Response.ERRO_DESCONHECIDO);
						progressDialog.dismiss();
					}
					
					return response;
				}
				
				public void retornoRegistro(Response response){						
						
					if(Response.SUCESSO == response.getCode() && response.isSucesso()){
						
						if(response instanceof ListaServicoResponse){							
							ListaServicoResponse lstResponse = (ListaServicoResponse) response;
							//prestarLista = ServiceBoxMobileUtil.preencherServico(lstResponse);
							mAdapter = new PrestarServicoListAdapter(getActivity(), prestarLista);
							mAdapter.setUrl(getString(R.string.ip_servidor_servicebox));
							listaPrestacaoServico.setAdapter(mAdapter);
							mAdapter.notifyDataSetChanged();
						}						
						
					}else{
						progressDialog.dismiss();
						GuiUtils.alert(response.getMessage());
					}	
									
				}	
				
				
				@Override
				protected void onCancelled() {
					super.onCancelled();
					progressDialog.dismiss();
				}
				
				
				
				@Override
				protected void onPostExecute(Response result) {				
					super.onPostExecute(result);
					progressDialog.dismiss();
					this.retornoRegistro(result);
					mAdapter.notifyDataSetChanged();
				}
					
		} // Fim	
			
		
		@Override
		public void onSaveInstanceState(Bundle outState) {
		  super.onSaveInstanceState(outState);
		  
		}
		
		@Override
		public void onDestroy() {
			super.onDestroy();
			
		}
		
		
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		 
			Info info = mAdapter.getItem(position);
					
			Intent intent = new Intent(getActivity(), InfoActivity.class);
		    intent.putExtra(InfoActivity.INFO_SERVICO, info);
		    getActivity().startActivity(intent);
		  
		  /**  usar da forma abaixo como no exemplo
		  @Override
		  public void onCreate(Bundle savedInstanceState) {
		      super.onCreate(savedInstanceState);
		      if (savedInstanceState != null) {
		          mCredentials = savedInstanceState.getParcelableArrayList(CREDENTIALS);
		      } else {
		          mCredentials = getActivity().getIntent().getParcelableArrayListExtra(CREDENTIALS);
		      }
		  }
		 
		} 
		
		@Override
		public void onSaveInstanceState(Bundle outState) {
		  super.onSaveInstanceState(outState);
		  outState.putParcelableArrayList(CREDENTIALS, mCredentials);
		}
		**/ 
		}
		
		}
}
