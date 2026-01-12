package com.example.smartnotes.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnotes.databinding.ItemNoteBinding
import com.example.smartnotes.models.Note
import android.view.animation.AnimationUtils
import com.example.smartnotes.R

class NotesAdapter(
    private var notes: List<Note>,
    private val onNoteClick: (Note) -> Unit,
    private val onMenuClick: (Note) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.apply {
                tvNoteTitle.text = note.title
                tvNoteContent.text = note.content
                tvTimestamp.text = note.getTimeAgo()
                chipCategory.text = note.category

                // Set category color
                val categoryColor = when (note.category) {
                    "Work" -> Color.parseColor("#F87171")
                    "Personal" -> Color.parseColor("#34D399")
                    "Study" -> Color.parseColor("#FBBF24")
                    "Ideas" -> Color.parseColor("#A78BFA")
                    else -> Color.parseColor("#6366F1")
                }
                gradientOverlay.setBackgroundColor(categoryColor)

                // Click animation
                root.setOnClickListener {
                    it.animate()
                        .scaleX(0.95f)
                        .scaleY(0.95f)
                        .setDuration(100)
                        .withEndAction {
                            it.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start()
                            onNoteClick(note)
                        }
                        .start()
                }

                btnMenu.setOnClickListener { onMenuClick(note) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.scale_up)
        holder.itemView.startAnimation(animation)
    }

    override fun getItemCount() = notes.size

    // ✅ Add this function to update notes list dynamically
    fun updateNotes(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}
