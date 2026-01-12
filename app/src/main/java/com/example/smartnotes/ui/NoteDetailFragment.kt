package com.example.smartnotes.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.smartnotes.R
import com.example.smartnotes.data.repository.GeminiRepository
import com.example.smartnotes.databinding.DialogAiResultBinding
import com.example.smartnotes.databinding.FragmentNoteDetailBinding
import com.example.smartnotes.models.Note
import com.example.smartnotes.utils.UiUtils
import kotlinx.coroutines.launch

class NoteDetailFragment : Fragment() {

    private var _binding: FragmentNoteDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by viewModels()
    private val geminiRepository = GeminiRepository()

    private var currentNoteId: Int? = null
    private var isEditMode = false

    companion object {
        private const val ARG_NOTE_ID = "note_id"
        private const val ARG_NOTE_TITLE = "note_title"
        private const val ARG_NOTE_CONTENT = "note_content"
        private const val ARG_NOTE_CATEGORY = "note_category"

        fun newInstance(note: Note?): NoteDetailFragment {
            val fragment = NoteDetailFragment()
            if (note != null) {
                val bundle = Bundle().apply {
                    putInt(ARG_NOTE_ID, note.id)
                    putString(ARG_NOTE_TITLE, note.title)
                    putString(ARG_NOTE_CONTENT, note.content)
                    putString(ARG_NOTE_CATEGORY, note.category)
                }
                fragment.arguments = bundle
            }
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadNoteIfEditing()
        setupToolbar()
        setupClickListeners()
    }

    private fun loadNoteIfEditing() {
        arguments?.let { args ->
            isEditMode = args.containsKey(ARG_NOTE_ID)

            if (isEditMode) {
                currentNoteId = args.getInt(ARG_NOTE_ID)
                binding.toolbar.title = "Edit Note"
                binding.etTitle.setText(args.getString(ARG_NOTE_TITLE))
                binding.etContent.setText(args.getString(ARG_NOTE_CONTENT))

                val category = args.getString(ARG_NOTE_CATEGORY)
                when (category) {
                    "Work" -> binding.chipWork.isChecked = true
                    "Personal" -> binding.chipPersonal.isChecked = true
                    "Study" -> binding.chipStudy.isChecked = true
                    "Ideas" -> binding.chipIdeas.isChecked = true
                }
            } else {
                binding.toolbar.title = "New Note"
                binding.chipStudy.isChecked = true
            }
        } ?: run {
            binding.toolbar.title = "New Note"
            binding.chipStudy.isChecked = true
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupClickListeners() {
        binding.fabSave.setOnClickListener {
            saveNote()
        }

        // AI Summarize
        binding.btnSummarize.setOnClickListener {
            val content = binding.etContent.text.toString().trim()
            if (content.isEmpty()) {
                Toast.makeText(context, "Add some content first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (content.length < 50) {
                Toast.makeText(context, "Content too short to summarize", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            summarizeNote(content)
        }

        // AI Flashcards
        binding.btnFlashcards.setOnClickListener {
            val content = binding.etContent.text.toString().trim()
            if (content.isEmpty()) {
                Toast.makeText(context, "Add some content first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (content.length < 100) {
                Toast.makeText(context, "Content too short for flashcards", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            generateFlashcards(content)
        }
    }

    private fun summarizeNote(content: String) {
        val dialogBinding = DialogAiResultBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialogBinding.apply {
            tvDialogTitle.text = "✨ AI Summary"
            loadingLayout.visibility = View.VISIBLE
            resultLayout.visibility = View.GONE

            btnClose.setOnClickListener { dialog.dismiss() }
            btnDone.setOnClickListener { dialog.dismiss() }
            btnCopy.setOnClickListener {
                copyToClipboard(tvResult.text.toString())
            }
        }

        dialog.show()

        // Make API call
        lifecycleScope.launch {
            val result = geminiRepository.summarizeText(content)

            dialogBinding.apply {
                loadingLayout.visibility = View.GONE
                resultLayout.visibility = View.VISIBLE

                result.onSuccess { summary ->
                    tvResult.text = summary
                }
                result.onFailure { error ->
                    tvResult.text = "Error: ${error.message}\n\nPlease check:\n• Internet connection\n• API key is correct\n• API quota not exceeded"
                    tvResult.setTextColor(resources.getColor(R.color.error, null))
                }
            }
        }
    }

    private fun generateFlashcards(content: String) {
        val dialogBinding = DialogAiResultBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialogBinding.apply {
            tvDialogTitle.text = "🎴 AI Flashcards"
            loadingLayout.visibility = View.VISIBLE
            resultLayout.visibility = View.GONE

            btnClose.setOnClickListener { dialog.dismiss() }
            btnDone.setOnClickListener { dialog.dismiss() }
            btnCopy.setOnClickListener {
                copyToClipboard(tvResult.text.toString())
            }
        }

        dialog.show()

        // Make API call
        lifecycleScope.launch {
            val result = geminiRepository.generateFlashcards(content, 5)

            dialogBinding.apply {
                loadingLayout.visibility = View.GONE
                resultLayout.visibility = View.VISIBLE

                result.onSuccess { flashcards ->
                    tvResult.text = flashcards
                }
                result.onFailure { error ->
                    tvResult.text = "Error: ${error.message}\n\nPlease check:\n• Internet connection\n• API key is correct\n• API quota not exceeded"
                    tvResult.setTextColor(resources.getColor(R.color.error, null))
                }
            }
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("AI Result", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    private fun saveNote() {
        val title = binding.etTitle.text.toString().trim()
        val content = binding.etContent.text.toString().trim()
        val category = getSelectedCategory()

        when {
            title.isEmpty() -> {
                binding.etTitle.error = "Title required"
                return
            }
            content.isEmpty() -> {
                UiUtils.showErrorSnackbar(binding.root, "Content cannot be empty")
                return
            }
        }

        if (isEditMode && currentNoteId != null) {
            val updatedNote = Note(
                id = currentNoteId!!,
                title = title,
                content = content,
                category = category,
                timestamp = System.currentTimeMillis()
            )
            viewModel.updateNote(updatedNote)
            UiUtils.showSuccessSnackbar(binding.root, "✓ Note updated successfully!")
        } else {
            val newNote = Note(
                title = title,
                content = content,
                category = category,
                timestamp = System.currentTimeMillis()
            )
            viewModel.insertNote(newNote)
            UiUtils.showSuccessSnackbar(binding.root, "✓ Note created successfully!")
        }

        binding.root.postDelayed({
            requireActivity().onBackPressed()
        }, 1000)
    }

    private fun getSelectedCategory(): String {
        val checkedId = binding.chipGroupCategory.checkedChipId
        return when (checkedId) {
            R.id.chipWork -> "Work"
            R.id.chipPersonal -> "Personal"
            R.id.chipStudy -> "Study"
            R.id.chipIdeas -> "Ideas"
            else -> "General"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


