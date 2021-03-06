package pl.edu.agh.borrowit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by lukasz on 25.11.15.
 * Adapter do listy znajdującej się nad buttonami
 */
public class AdapterDoListy extends RecyclerView.Adapter<AdapterDoListy.VH> {

    private Context context;
    private List<Model> dataSet = new ArrayList<>();

    public AdapterDoListy(Context context) {
        this.context = context;
        refreshAll();
    }

    public void update() {
        refreshAll();
    }

    private void refreshWith(List<Model> data) {
        dataSet.addAll(data);
        notifyItemRangeChanged(0, getItemCount());
        notifyDataSetChanged();
    }

    /**
     * tworzenie widoku
     *
     * @param viewGroup
     * @param i
     * @return
     */
    @Override
    public VH onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        return new VH(v);
    }


    /**
     * przeskalowanie obrazu do wymiaru 100x100 aby zmniejszyć rozmiar bitmapy
     * @param path
     * @return
     */
    private Bitmap scaledBMP(String path) {
        return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path), 100, 100, false);
    }

    /**
     * odświeżenie pojedynczego elementu listy
     *
     * @param vh
     * @param position
     */
    @Override
    public void onBindViewHolder(VH vh, int position) {
        final Model model = dataSet.get(position);
        final Bitmap pencilImage = scaledBMP(model.getImageFilePath());
        final Bitmap kasztan = scaledBMP(model.getBorrowerFilePath());

        vh.number.setText(model.getPhoneNumber());
        vh.borrower.setImageBitmap(kasztan);
        vh.pencil.setImageBitmap(pencilImage);
        /**
         * po przyciśnięciu itema, usuwamy go:
         */
        vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Realm realm = Realm.getInstance(context);
                realm.beginTransaction();
                RealmResults<Model> toDelete = realm.where(Model.class)
                        .equalTo("primaryKey", model.getPrimaryKey()).findAll();
                toDelete.clear();
                realm.commitTransaction();
                refreshAll();
                Toast.makeText(context, "Pair deleted", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void refreshAll() {
        dataSet.clear();
        Realm realm = Realm.getInstance(context);
        RealmResults<Model> results = realm.where(Model.class).findAll();
        refreshWith(results);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        public ImageView pencil, borrower;
        public TextView number;
        public VH(View itemView) {
            super(itemView);
            /**
             * itemy znalezione w widoku R.layout.item (należy nadać im unikalne ID aby móc je tu przypisać)
             */
            pencil = (ImageView) itemView.findViewById(R.id.pencil);
            borrower = (ImageView) itemView.findViewById(R.id.borrower);
            number = (TextView) itemView.findViewById(R.id.number);

        }
    }
}
