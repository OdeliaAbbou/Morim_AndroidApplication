package com.example.morim.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.morim.ChatActivity;
import com.example.morim.R;
import com.example.morim.adapter.TeacherAdapter;
import com.example.morim.database.OnDataCallback;
import com.example.morim.databinding.FragmentTeacherBinding;
import com.example.morim.model.Favorite;
import com.example.morim.model.Teacher;
import com.example.morim.model.User;
import com.example.morim.ui.BaseFragment;
import com.example.morim.ui.dialog.RatingListDialog;
import com.example.morim.viewmodel.MainViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TeacherFragment extends BaseFragment implements TeacherAdapter.ScheduleClickListener {


    private FragmentTeacherBinding binding;

    private MainViewModel mainViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTeacherBinding.inflate(inflater, container, false);
        mainViewModel = activityScopedViewModel(MainViewModel.class);
        return binding.getRoot();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            String teacherStr = TeacherFragmentArgs.fromBundle(getArguments()).getTeacher();
            Gson g = new Gson();
            binding.backBtn.setOnClickListener(v -> findNavController().popBackStack());

            Teacher t = g.fromJson(teacherStr, Teacher.class);

            String currentUserId = FirebaseAuth.getInstance().getUid();
            hideButtonsIfOwnProfile(t);

            mainViewModel.getCurrentUserOnce(new OnDataCallback<User>() {
                @Override
                public void onData(User currentUser) {
                    mainViewModel.getMyMeetingsData().observe(getViewLifecycleOwner(), myMeetingsData -> {
                        binding.btnAddReview.setVisibility(View.GONE);

                        if (myMeetingsData != null && myMeetingsData.allResourcesAvailable()) {
                            boolean hasMeeting = myMeetingsData.getMyMeetings().stream()
                                    .anyMatch(meeting ->
                                            meeting.getTeacherId().equals(t.getId()) &&
                                                    meeting.getStudentId().equals(FirebaseAuth.getInstance().getUid()) &&
                                                    !meeting.isCanceled() &&
                                                    meeting.getMeetingDate() != null &&
                                                    meeting.getMeetingDate() < System.currentTimeMillis()
                                    );

                            boolean hasReviewed = currentUser.getComments().stream()
                                    .anyMatch(comment -> comment.getTeacherId().equals(t.getId()));

                            if (hasMeeting && !hasReviewed) {
                                binding.btnAddReview.setVisibility(View.VISIBLE);
                                binding.btnAddReview.setOnClickListener(v -> {
                                    mainViewModel.showAddReviewDialog(
                                            requireContext(),
                                            getChildFragmentManager(),
                                            t,
                                            currentUser
                                    );
                                });
                            }
                        }
                    });
                }

                @Override
                public void onException(Exception e) {
                    Toast.makeText(requireContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


            binding.tvPrice.setText(String.format("%.1f$ /hour", t.getPrice()));
            binding.titleTeacher.setText(String.format("%s's Profile", t.getFullName()));
            binding.tvTeacherItemName.setText(t.getFullName());
            binding.rbTeacherItem.setRating((float) t.getAverageRating());
            if (t.getTeachingSubjects() != null && !t.getTeachingSubjects().isEmpty()) {
                binding.tvTeacherItemSubjects.setText(
                        String.join("\n", t.getTeachingSubjects()));
            } else {
                binding.tvTeacherItemSubjects.setText("Non spécifiés");
            }

            Log.d("ImageURL", "URL de l'image : " + t.getImage());

            if (t.getEducation() != null && !t.getEducation().isEmpty()) {
                binding.tvTeacherItemEducation.setText( t.getEducation());
            } else {
                binding.tvTeacherItemEducation.setText("Non spécifiée");
            }
            binding.tvTeacherPhone.setText( (t.getPhone() != null && !t.getPhone().isEmpty()
                    ? t.getPhone()
                    : "Not specified"));

            binding.tvTeacherEmail.setText((t.getEmail() != null && !t.getEmail().isEmpty()
                    ? t.getEmail()
                    : "Not specified"));

            binding.tvTeacherAddress.setText((t.getAddress() != null && !t.getAddress().isEmpty()
                    ? t.getAddress()
                    : "Not specified"));

            if (t.getPhone() != null && !t.getPhone().isEmpty()) {
                binding.tvTeacherPhone.setText(t.getPhone());
                binding.tvTeacherPhone.setTextColor(getResources().getColor(R.color.teal_700));
                binding.tvTeacherPhone.setPaintFlags(binding.tvTeacherPhone.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
                binding.tvTeacherPhone.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(android.net.Uri.parse("tel:" + t.getPhone()));
                    startActivity(intent);
                });
            } else {
                binding.tvTeacherPhone.setText("Not specified");
                binding.tvTeacherPhone.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }

            if (t.getEmail() != null && !t.getEmail().isEmpty()) {
                binding.tvTeacherEmail.setText(t.getEmail());
                binding.tvTeacherEmail.setTextColor(getResources().getColor(R.color.teal_700));
                binding.tvTeacherEmail.setPaintFlags(binding.tvTeacherEmail.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
                binding.tvTeacherEmail.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(android.net.Uri.parse("mailto:" + t.getEmail()));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Lesson inquiry");
                    startActivity(intent);
                });
            } else {
                binding.tvTeacherEmail.setText("Not specified");
                binding.tvTeacherEmail.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }

            if (t.getAddress() != null && !t.getAddress().isEmpty()) {
                binding.tvTeacherAddress.setText(t.getAddress());
                binding.tvTeacherAddress.setTextColor(getResources().getColor(R.color.teal_700));
                binding.tvTeacherAddress.setPaintFlags(binding.tvTeacherAddress.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
                binding.tvTeacherAddress.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("waze://?q=" + t.getAddress()));
                        startActivity(intent);
                    } catch (Exception e) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("geo:0,0?q=" + t.getAddress()));
                        startActivity(intent);
                    }
                });
            } else {
                binding.tvTeacherAddress.setText("Not specified");
                binding.tvTeacherAddress.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }



            mainViewModel.getMyFavorites()
                    .observe(getViewLifecycleOwner(), favorites -> {
                        if (favorites == null) {
                            return;
                        }
                        Set<Favorite> favSet = new HashSet<>(favorites);
                        if (favSet.contains(new Favorite(t.getId()))) {
                            binding.btnFavorite.setImageResource(R.drawable.baseline_favorite_24);
                        } else {
                            binding.btnFavorite.setImageResource(R.drawable.ic_favorite);
                        }
                    });

            binding.btnFavorite.setOnClickListener(v -> {
                List<Favorite> favorites = mainViewModel.getMyFavorites().getValue();
                if (favorites == null) {
                    return;
                }
                Set<Favorite> favSet = new HashSet<>(favorites);
                if(favSet.contains(new Favorite(t.getId()))){
                    mainViewModel.removeFavorite(t.getId());
                    return;
                }
                mainViewModel.addFavorite(t.getId());
            });
            binding.btnChat.setOnClickListener(v -> {
                String teacherId = t.getId();
                if (teacherId == null || teacherId.isEmpty()) {
                    Toast.makeText(requireContext(), "Erreur : L'ID de l'enseignant est introuvable", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (teacherId.equals(currentUserId)) {
                    Toast.makeText(requireContext(), "Can't converse with yourself.", Toast.LENGTH_SHORT).show();
                    return;
                }



                Log.d("onSendMessage", t.toString());
                Intent intent = new Intent(requireContext(), ChatActivity.class);
                intent.putExtra("TEACHER_ID", teacherId);
                intent.putExtra("TEACHER_NAME", t.getFullName());
                requireContext().startActivity(intent);
            });
            if (!t.getImage().isEmpty()) {
                Picasso.get()
                        .load(t.getImage())
                        .into(binding.ivTeacherItem);
            } else {
                Picasso.get()
                        .load(User.DEFAULT_IMAGE)
                        .into(binding.ivTeacherItem);
            }
            binding.btnCommentsList.setOnClickListener(v -> {
                if (t.getComments().size() > 0) {
                    RatingListDialog.showRatingListDialog(t.getComments(), getChildFragmentManager());
                } else {
                    Toast.makeText(view.getContext(), "No reviews available", Toast.LENGTH_SHORT).show();
                }
            });

            binding.btnSchedule.setOnClickListener(v ->
                    onRequestScheduleWithTeacher(t));
            if (t.getId().equals(FirebaseAuth.getInstance().getUid())) {
                binding.rbTeacherItem.setEnabled(false);
            } else if (t.getRatingStudents().contains(FirebaseAuth.getInstance().getUid())) {
                binding.rbTeacherItem.setEnabled(false);
            } else {
                binding.rbTeacherItem.setOnRatingBarChangeListener((ratingBar, rating, b) -> {
                    mainViewModel.rateTeacher(t, rating);
                    binding.rbTeacherItem.setRating((float) t.getAverageRating());
                    Snackbar.make(binding.getRoot(), "Rated teacher!", Snackbar.LENGTH_LONG).show();
                });
            }

        } else {
            findNavController()
                    .popBackStack();
        }

    }

    @Override
    public void onRequestScheduleWithTeacher(Teacher teacher) {
        String cid = FirebaseAuth.getInstance().getUid();
        if (cid != null && cid.equals(teacher.getId())) {
            Snackbar.make(binding.getRoot(), "Cannot schedule with your self", Snackbar.LENGTH_LONG).show();
            return;
        }
        mainViewModel.showScheduleMeetingDialog(
                requireContext(),
                getChildFragmentManager(),
                teacher
        );

    }
    private void hideButtonsIfOwnProfile(Teacher teacher) {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        if (teacher.getId().equals(currentUserId)) {
            binding.btnSchedule.setVisibility(View.GONE);
            binding.btnChat.setVisibility(View.GONE);
            binding.btnFavorite.setVisibility(View.GONE);
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
