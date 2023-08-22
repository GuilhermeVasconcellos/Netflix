package com.example.projetonetflixapi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projetonetflixapi.R
import com.example.projetonetflixapi.api.RetrofitService
import com.example.projetonetflixapi.databinding.ItemFilmeBinding
import com.example.projetonetflixapi.model.Filme
import com.squareup.picasso.Picasso

class FilmeAdapter(
    val onClick: (Filme) -> Unit
) : RecyclerView.Adapter<FilmeAdapter.FilmeViewHolder>() {

    var listaFilmes = mutableListOf<Filme>()

    fun adicionarLista(lista: List<Filme>) {
        listaFilmes.addAll(lista)
        notifyDataSetChanged()
    }

    inner class FilmeViewHolder(val itemFilme: ItemFilmeBinding)
        : RecyclerView.ViewHolder(itemFilme.root) {

            /*private val binding: ItemFilmeBinding
            init {
                this.binding = itemFilme
            }*/

        fun bind(filme: Filme) {
            val nomeImagem = filme.backdrop_path
            val tamanhoImagem = "w780"
            val urlImagem = RetrofitService.BASE_URL_IMAGE
            val url = urlImagem + tamanhoImagem + nomeImagem

            Picasso.get()
                .load(url)
                .placeholder(R.drawable.carregando)
                .into(itemFilme.imgItemFilme)
            itemFilme.textTitulo.text = filme.title
            itemFilme.clItem.setOnClickListener {
                onClick(filme)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmeViewHolder {
        val itemFilmeBinding = ItemFilmeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FilmeViewHolder(itemFilmeBinding)
    }

    override fun onBindViewHolder(holder: FilmeViewHolder, position: Int) {
        val filme = listaFilmes[position]
        holder.bind(filme)
    }

    override fun getItemCount(): Int {
        return listaFilmes.size
    }


}