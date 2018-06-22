package xyz.hvosdt.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecycleView;
    private CrimeAdapter mAdapter;
    private Button mAddCrimeButton;
    private static final int REQUEST_CRIME = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        List<Crime> empty = chechCrimes();
        if (!empty.isEmpty()) {
            View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
            mCrimeRecycleView = (RecyclerView) view.findViewById(R.id.crime_recycle_view);
            mCrimeRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
            updateUI();
            return view;
        }else {
            View view = inflater.inflate(R.layout.fragment_empty_crime_list, container, false);
            mAddCrimeButton = (Button) view.findViewById(R.id.add_crime_button);
            mAddCrimeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Crime crime = new Crime();
                    CrimeLab.get(getActivity()).add(crime);
                    Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                    startActivity(intent);
                }
            });
            return view;
        }


    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Crime mCrime;
        private Date mDate;
        private ImageView mSolvedImageView;

        public CrimeHolder (LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));


            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
            itemView.setOnClickListener(this);
        }

        public void bind (Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(convertDate(crime.getDate()));
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter (List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }

    private List<Crime> chechCrimes() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        return crimes;
    }

    private void updateUI() {
        List<Crime> crimes = chechCrimes();
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            if (mCrimeRecycleView != null) {
                mCrimeRecycleView.setAdapter(mAdapter);
            };
        }else {
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime :
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).add(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);
                return true;
            case R.id.show_subtitle :
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle () {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private String convertDate (Date date) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        String result = df.format(date);
        return result;
    }
}
