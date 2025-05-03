package cit.edu.pawfectmatch.ui.home;

import static cit.edu.pawfectmatch.LoginActivity.BASE_URL;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.GsonBuilder;

import cit.edu.pawfectmatch.R;
import cit.edu.pawfectmatch.network.ApiService;
import cit.edu.pawfectmatch.ui.home.HomePetAdapter;
import cit.edu.pawfectmatch.ui.home.PetFeedResponse;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment implements HomePetAdapter.OnPetClickListener {

    private RecyclerView recyclerView;
    private HomePetAdapter petAdapter;
    private ApiService apiService;
//    private ProgressBar progressBar;
    private TextView emptyStateTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HomeViewModel viewModel;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_pets);
//        progressBar = view.findViewById(R.id.progress_bar);
        emptyStateTextView = view.findViewById(R.id.empty_state_text);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        petAdapter = new HomePetAdapter(this);
        recyclerView.setAdapter(petAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();
        apiService = retrofit.create(ApiService.class);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        observeViewModel();
        setupSwipeRefresh();
        setupPagination();

        // Fetch pets only if no data is cached
        if (viewModel.getPets().getValue() == null || viewModel.getPets().getValue().isEmpty()) {
            viewModel.fetchPets(requireContext(), apiService, false);
        }

        return view;
    }

    private void observeViewModel() {
        viewModel.getPets().observe(getViewLifecycleOwner(), pets -> {
            petAdapter.setPets(pets);
            emptyStateTextView.setVisibility(pets.isEmpty() ? View.VISIBLE : View.GONE);
            emptyStateTextView.setText(pets.isEmpty() ? "No pets available" : "");
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
//            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            swipeRefreshLayout.setRefreshing(isLoading);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                emptyStateTextView.setText(error.contains("log in") ? error : "Error loading pets. Tap to retry");
                emptyStateTextView.setVisibility(View.VISIBLE);
                emptyStateTextView.setOnClickListener(v -> viewModel.fetchPets(requireContext(), apiService, false));
            } else {
                emptyStateTextView.setOnClickListener(null);
            }
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.fetchPets(requireContext(), apiService, true);
        });
    }

    private void setupPagination() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (viewModel.getIsLoading().getValue() != null && viewModel.getIsLoading().getValue()) return;

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5 && totalItemCount > 0) {
                    viewModel.fetchPets(requireContext(), apiService, false);
                }
            }
        });
    }

    @Override
    public void onPetClick(PetFeedResponse pet) {
        // TODO: Navigate to pet details screen using pet.getPetId()
        Toast.makeText(getContext(), "Clicked: " + pet.getName(), Toast.LENGTH_SHORT).show();
    }
}