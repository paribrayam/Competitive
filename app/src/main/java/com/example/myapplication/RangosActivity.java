package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class RangosActivity extends AppCompatActivity {
    private static final int[] RANGO_IMAGES = {
        R.drawable.cuarzo, R.drawable.agata, R.drawable.amatista, R.drawable.topacio, R.drawable.opalo,
        R.drawable.zafiro, R.drawable.esmeralda, R.drawable.rubi, R.drawable.diamante, R.drawable.alejandrita
    };
    private static final String[] RANGO_NAMES = {
        "Cuarzo", "Ágata", "Amatista", "Topacio", "Ópalo", "Zafiro", "Esmeralda", "Rubí", "Diamante", "Alejandrita"
    };
    private static final String[] RANGO_DESCS = {
        "El rango inicial, símbolo de nuevos comienzos y potencial.",
        "Más resistente, representa la constancia y el avance.",
        "Un rango de sabiduría y creatividad en el camino.",
        "Brilla con energía y optimismo, un logro notable.",
        "Raro y especial, símbolo de adaptabilidad y cambio.",
        "Un rango de gran valor, asociado a la claridad y la verdad.",
        "Representa crecimiento, esperanza y logros importantes.",
        "Un rango de pasión, fuerza y determinación.",
        "Muy valioso, símbolo de excelencia y perfección.",
        "El rango supremo, reservado para los más dedicados y expertos."
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rangos);
        ViewPager2 viewPager = findViewById(R.id.viewpager_rangos);
        viewPager.setAdapter(new RangosAdapter());
    }
    class RangosAdapter extends RecyclerView.Adapter<RangosAdapter.RangoViewHolder> {
        @NonNull
        @Override
        public RangoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rango, parent, false);
            return new RangoViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull RangoViewHolder holder, int position) {
            holder.iv.setImageResource(RANGO_IMAGES[position]);
            holder.tvName.setText(RANGO_NAMES[position]);
            holder.tvDesc.setText(RANGO_DESCS[position]);
        }
        @Override
        public int getItemCount() {
            return RANGO_IMAGES.length;
        }
        class RangoViewHolder extends RecyclerView.ViewHolder {
            ImageView iv;
            TextView tvName, tvDesc;
            RangoViewHolder(@NonNull View itemView) {
                super(itemView);
                iv = itemView.findViewById(R.id.iv_rango_image);
                tvName = itemView.findViewById(R.id.tv_rango_name);
                tvDesc = itemView.findViewById(R.id.tv_rango_desc);
            }
        }
    }
} 