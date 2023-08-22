package com.example.projetonetflixapi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projetonetflixapi.databinding.ActivityItemPesquisaBinding
import com.example.projetonetflixapi.model.Filme

class PesquisaAdapter(
    val onClick: (Filme) -> Unit
): RecyclerView.Adapter<PesquisaAdapter.PesquisaViewHolder>() {

    var listaFilmes = mutableListOf<Filme>()

    fun adicionarLista(lista: List<Filme>) {
        listaFilmes.addAll(lista)
        notifyDataSetChanged()
    }

    inner class PesquisaViewHolder(val itemPesquisa: ActivityItemPesquisaBinding)
        : RecyclerView.ViewHolder(itemPesquisa.root) {

        fun bind(filme: Filme) {
            val nomeFilme = filme.title
            val idFilme = filme.id.toString()

            itemPesquisa.tvFilme.text = nomeFilme
//            itemPesquisa.tvFilme.text = idFilme
            itemPesquisa.clItemPesquisa.setOnClickListener {
                onClick(filme)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PesquisaViewHolder {
        val itemPesquisaBinding = ActivityItemPesquisaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PesquisaViewHolder(itemPesquisaBinding)
    }

    override fun onBindViewHolder(holder: PesquisaAdapter.PesquisaViewHolder, position: Int) {
        val filme = listaFilmes[position]
        holder.bind(filme)
    }

    override fun getItemCount(): Int {
        return listaFilmes.size
    }
}

