package com.example.morim.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.morim.AuthActivity;
import com.example.morim.ChatActivity;
import com.example.morim.R;
import com.example.morim.adapter.TeacherAdapter;
import com.example.morim.database.OnDataCallback;
import com.example.morim.databinding.FragmentHomeBinding;
import com.example.morim.model.Favorite;
import com.example.morim.model.Meeting;
import com.example.morim.model.MyMeetingsData;
import com.example.morim.model.Student;
import com.example.morim.model.Teacher;
import com.example.morim.model.User;
import com.example.morim.ui.BaseFragment;
import com.example.morim.ui.dialog.RatingDialog;
import com.example.morim.ui.dialog.RatingListDialog;
import com.example.morim.ui.dialog.ScheduleMeetingDialog;
import com.example.morim.ui.dialog.SubjectDialog;
import com.example.morim.util.DateUtils;
import com.example.morim.viewmodel.MainViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends BaseFragment implements TeacherAdapter.TeacherAdapterListener {
    private MainViewModel mainViewModel;
    private FragmentHomeBinding viewBinding;
    private User currentUser;
    private TeacherAdapter adapter;
    private Student stud;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentHomeBinding.inflate(inflater, container, false);
        mainViewModel = activityScopedViewModel(MainViewModel.class);
        return viewBinding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewBinding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewModel.signOut(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        requireActivity().finish();
                        Intent n = new Intent(requireContext(), AuthActivity.class);
                        n.putExtra("LOGOUT", true);
                        startActivity(n);
                    }
                });

            }
        });

        viewBinding.rvTeachers.setLayoutManager(new LinearLayoutManager(getContext()));

        mainViewModel.getTeachers()
                .observe(getViewLifecycleOwner(), teachers -> {
                    adapter = new TeacherAdapter(teachers, HomeFragment.this);
                    viewBinding.rvTeachers.setAdapter(adapter);
                    List<Favorite> favorites = mainViewModel.getMyFavorites().getValue();
                    if(adapter.noFavoritesYet() && favorites!=null) {
                        adapter.setFavorites(new HashSet<>(favorites));
                    }
                });

        mainViewModel.getMyFavorites()
                .observe(getViewLifecycleOwner(), favorites -> {
                    if (adapter != null && favorites != null) {
                        adapter.setFavorites(new HashSet<>(favorites));
                    }
                });

        mainViewModel.getCurrentUserOnce(new OnDataCallback<User>() {
            @Override
            public void onData(User user) {
                currentUser = user;

                if (user != null) {
                    viewBinding.titleMorim.setText("Hi " + user.getFullName());
                    if (user.getImage() != null && !user.getImage().isEmpty()) {
                        Picasso.get()
                                .load(user.getImage())
                                .placeholder(R.drawable.profile_ic)
                                .error(R.drawable.profile_ic)
                                .into(viewBinding.imgProfile);
                    } else {
                        viewBinding.imgProfile.setImageResource(R.drawable.profile_ic);
                    }
                }

                if (adapter != null) {
                    adapter.updateCurrentUser(user);
                }


                else {
                    Log.d("HomeFragment", "onChanged: User is not student");
                }
            }

            @Override
            public void onException(Exception e) {
            }
        });

        mainViewModel.getMyMeetingsData().observe(getViewLifecycleOwner(), new Observer<MyMeetingsData>() {
            @Override
            public void onChanged(MyMeetingsData myMeetingsData) {
                if (adapter != null && myMeetingsData.allResourcesAvailable()) {
                    adapter.updateCurrentMeetings(myMeetingsData.getMyMeetings());
                }
            }
        });
    }

    @Override
    public void onSendMessage(Teacher teacher) {
        String teacherId = teacher.getId();
        if (teacherId == null || teacherId.isEmpty()) {
            Toast.makeText(requireContext(), "Erreur : L'ID de l'enseignant est introuvable", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("onSendMessage", teacher.toString());
        // Lancer ChatActivity avec les informations nécessaires
        Intent intent = new Intent(requireContext(), ChatActivity.class);
        intent.putExtra("TEACHER_ID", teacherId);
        intent.putExtra("TEACHER_NAME", teacher.getFullName());
        requireContext().startActivity(intent);
    }

@Override
public void onAddReview(Teacher teacher) {
    mainViewModel.showAddReviewDialog(requireContext(), getChildFragmentManager(), teacher, currentUser);
}

    @Override
    public void onShowComments(Teacher teacher) {
        RatingListDialog.showRatingListDialog(teacher.getComments(), getChildFragmentManager());
    }

    @Override
    public void onAddToFavorites(Teacher teacher) {
        mainViewModel.addFavorite(teacher.getId())
                .addOnSuccessListener(favorite -> {
                    Snackbar.make(viewBinding.getRoot(), "Teacher added to favorites", Snackbar.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(viewBinding.getRoot(), "Failed to add teacher to favorites", Snackbar.LENGTH_LONG).show();
                });
    }

    @Override
    public void onRemoveFromFavorites(Teacher teacher) {
        mainViewModel.removeFavorite(teacher.getId())
                .addOnSuccessListener(favorite -> {
                    Snackbar.make(viewBinding.getRoot(), "Teacher removed from favorites", Snackbar.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(viewBinding.getRoot(), "Failed to remove teacher from favorites", Snackbar.LENGTH_LONG).show();
                });
    }

    @Override
    public void onRequestScheduleWithTeacher(Teacher teacher) {
        String cid = FirebaseAuth.getInstance().getUid();
        if (cid != null && cid.equals(teacher.getId())) {
            Snackbar.make(viewBinding.getRoot(), "Cannot schedule with your self", Snackbar.LENGTH_LONG).show();
            return;
        }

        mainViewModel.showScheduleMeetingDialog(
                requireContext(),
                getChildFragmentManager(),
                teacher
        );

    }

    @Override
    public void onViewTeacher(Teacher t) {
        Gson g = new Gson();
        HomeFragmentDirections.ActionHomeFragmentToTeacherFragment intent = HomeFragmentDirections.actionHomeFragmentToTeacherFragment(g.toJson(t));
        findNavController()
                .navigate(intent);
    }
}

