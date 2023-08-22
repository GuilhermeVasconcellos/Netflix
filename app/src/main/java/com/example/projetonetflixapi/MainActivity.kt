package com.example.projetonetflixapi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.projetonetflixapi.adapter.FilmeAdapter
import com.example.projetonetflixapi.adapter.PesquisaAdapter
import com.example.projetonetflixapi.api.FilmeAPI
import com.example.projetonetflixapi.api.RetrofitService
import com.example.projetonetflixapi.api.RetrofitService.Companion.retrofit
import com.example.projetonetflixapi.databinding.ActivityMainBinding
import com.example.projetonetflixapi.model.FilmeRecente
import com.example.projetonetflixapi.model.FilmeResposta
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val TAG = "info_filme"
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val filmeAPI by lazy {
        RetrofitService.filmeAPI
    }
    var jobFilmeRecente: Job? = null
    var jobFilmesPopulares: Job? = null
    var jobPesquisar: Job? = null

    var filmeAdapter: FilmeAdapter? = null
    var paginaAtual: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        filmeAdapter = FilmeAdapter {
            val intent = Intent(this, DetalhesActivity::class.java)
            intent.putExtra("idFilme", it.id)
            Log.i(TAG, "idFilme: ${it.id} - Título: ${it.title}")
//            intent.putExtra("filme", it)
            startActivity(intent)
        }
        binding.rvPopulares.adapter = filmeAdapter

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.rvPopulares.layoutManager = layoutManager
        
        // adicionar evento de scroll
        binding.rvPopulares.addOnScrollListener(object: OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)

//                Log.i(TAG, "onScrolled: dx: $dx dy: $dy")
                val totalItens = recyclerView.adapter?.itemCount
                val ultimoItem = layoutManager.findLastVisibleItemPosition()

                if((totalItens?.minus(1)) == ultimoItem) {
                    binding.fabAdicionar.hide()
                }else {
                    binding.fabAdicionar.show()
                }

                // scroll infinito
                val retorno = recyclerView.canScrollHorizontally(1)


                if(!retorno) {
                    // carregar próxima página
                    if(paginaAtual < 1000) {
                        Log.i(TAG, "Página atual: $paginaAtual")
                        recuperarFilmesPopulares(++paginaAtual)
                    }
                }

            }
        })

        binding.btnPesquisar.setOnClickListener {
            val intent = Intent(this, PesquisaActivity::class.java)
            intent.putExtra("nome", binding.editPesquisa.text.toString())
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        recuperarFilmeRecente()
        recuperarFilmesPopulares(paginaAtual)
    }

    override fun onStop() {
        super.onStop()
        jobFilmeRecente?.cancel()
        jobFilmesPopulares?.cancel()
        jobPesquisar?.cancel()
        finish()
    }


    private fun recuperarFilmesPopulares(pagina: Int) {
        jobFilmesPopulares = CoroutineScope(Dispatchers.IO).launch {
            var resposta: Response<FilmeResposta>? = null

            try {
                resposta = filmeAPI.recuperarFilmesPopulares(pagina)
            }catch (e: Exception) {
                e.printStackTrace()
                exibirMensagem("Erro ao fazer a requisição.")
            }

            if(resposta != null) {
                if(resposta.isSuccessful) {
                    val listaFilmes = resposta.body()?.results
                    if(listaFilmes != null && listaFilmes.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            filmeAdapter?.adicionarLista(listaFilmes)
                        }

                        listaFilmes?.forEach {
                            Log.i(TAG, "Título: ${it.title} - ID: ${it.id}")
                        }
                    }


                }else {
                    exibirMensagem("Não foi possível recuperar a lista de filmes populares. Código: ${resposta.code()}")
                }
            }else {
                exibirMensagem("Não foi possível fazer a requisição")
            }
        }
    }

    private fun recuperarFilmeRecente() {
        jobFilmeRecente = CoroutineScope(Dispatchers.IO).launch {
            var resposta: Response<FilmeRecente>? = null

            try {
                resposta = filmeAPI.recuperarFilmeRecente()
            }catch (e: Exception) {
                e.printStackTrace()
                exibirMensagem("Erro ao fazer a requisição.")
            }

            if(resposta != null) {
                if(resposta.isSuccessful) {
                    val filmeRecente = resposta.body()
                    val nomeImagem = filmeRecente?.poster_path
                    val url = RetrofitService.BASE_URL_IMAGE + "w780" + nomeImagem

                    withContext(Dispatchers.Main) {
                        Picasso.get()
                            .load(url)
                            .error(R.drawable.capa)
                            .into(binding.imgCapa)
                    }
                }else {
                    exibirMensagem("Não foi possível recuperar o filme recente. Código: ${resposta.code()}")
                }
            }else {
                exibirMensagem("Não foi possível fazer a requisição")
            }
        }
    }

    private fun exibirMensagem(mensagem: String) {
        Toast.makeText(applicationContext, mensagem, Toast.LENGTH_LONG).show()
    }

}