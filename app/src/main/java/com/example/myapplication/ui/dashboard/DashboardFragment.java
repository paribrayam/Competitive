package com.example.myapplication.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.PrivacyPolicyActivity;
import com.example.myapplication.databinding.FragmentDashboardBinding;
import com.example.myapplication.StudentToolsActivity;
import com.example.myapplication.CalculatorActivity;
import com.example.myapplication.CalendarActivity;
import com.example.myapplication.DrawingActivity;
import com.example.myapplication.WebBrowserActivity;
import com.example.myapplication.TranslatorActivity;
import com.example.myapplication.GeminiActivity;
import com.example.myapplication.PixelArtActivity;
import com.example.myapplication.R;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        
        binding.btnPrivacyPolicy.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PrivacyPolicyActivity.class);
            startActivity(intent);
        });
        
        binding.btnStudentTools.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), StudentToolsActivity.class);
            startActivity(intent);
        });
        
        binding.btnCalculator.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CalculatorActivity.class);
            startActivity(intent);
        });
        
        binding.btnCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CalendarActivity.class);
            startActivity(intent);
        });
        
        binding.btnDrawing.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DrawingActivity.class);
            startActivity(intent);
        });
        
        binding.btnBrowser.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WebBrowserActivity.class);
            startActivity(intent);
        });
        
        binding.btnTranslator.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TranslatorActivity.class);
            startActivity(intent);
        });
        
        binding.btnPixelArt.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PixelArtActivity.class);
            startActivity(intent);
        });
        
        ImageButton btnGeminiImage = root.findViewById(R.id.btn_gemini_image);
        btnGeminiImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VOICE_COMMAND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(intent);
            } catch (Exception e) {
                // Fallback: intenta abrir el asistente estándar
                Intent intent2 = new Intent(Intent.ACTION_ASSIST);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent2);
                } catch (Exception ex) {
                    Toast.makeText(getContext(), "No se pudo abrir el asistente", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}