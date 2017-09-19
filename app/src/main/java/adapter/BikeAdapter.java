package adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.vitor.usebike.R;

import java.util.ArrayList;

import modelo.Bike;

/**
 * Created by maxwe on 18/09/2017.
 */

public class BikeAdapter extends BaseAdapter implements ListAdapter {
    private Activity act;
    private ArrayList<Bike> listadebikes;

    public BikeAdapter(Activity actparam, ArrayList<Bike> listaparam){
        this.act = actparam;
        this.listadebikes = listaparam;
    }

    @Override
    public int getCount() {
        return listadebikes.size();
    }

    @Override
    public Object getItem(int position) {
        return listadebikes.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    public String getItemNome(int position) {
        return listadebikes.get(position).getNome();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listadebikes_layout,parent,false);
        }

        TextView identificacao = (TextView) convertView.findViewById(R.id.lblListaNomeBike);
        TextView latitude = (TextView) convertView.findViewById(R.id.lblListaLatitude);
        TextView longitude = (TextView) convertView.findViewById(R.id.lblListaLongitude);

        Bike b = listadebikes.get(position);
        identificacao.setText(b.getNome());
        longitude.setText(b.getLatitude());
        latitude.setText(b.getLatitude());

        return convertView;
    }

}
