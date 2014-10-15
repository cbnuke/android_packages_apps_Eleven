/*
 * Copyright (C) 2012 Andrew Neal Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.cyngn.eleven.ui.fragments.phone;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cyngn.eleven.R;
import com.cyngn.eleven.adapters.PagerAdapter;
import com.cyngn.eleven.adapters.PagerAdapter.MusicFragments;
import com.cyngn.eleven.menu.CreateNewPlaylist;
import com.cyngn.eleven.ui.fragments.AlbumFragment;
import com.cyngn.eleven.ui.fragments.ArtistFragment;
import com.cyngn.eleven.ui.fragments.BaseFragment;
import com.cyngn.eleven.ui.fragments.SongFragment;
import com.cyngn.eleven.utils.MusicUtils;
import com.cyngn.eleven.utils.PreferenceUtils;
import com.cyngn.eleven.utils.SortOrder;
import com.viewpagerindicator.TabPageIndicator;

/**
 * This class is used to hold the {@link ViewPager} used for swiping between the
 * playlists, recent, artists, albums, songs, and genre {@link Fragment}
 * s for phones.
 * 
 * @NOTE: The reason the sort orders are taken care of in this fragment rather
 *        than the individual fragments is to keep from showing all of the menu
 *        items on tablet interfaces. That being said, I have a tablet interface
 *        worked out, but I'm going to keep it in the Play Store version of
 *        Apollo for a couple of weeks or so before merging it with CM.
 * @author Andrew Neal (andrewdneal@gmail.com)
 */
public class MusicBrowserPhoneFragment extends BaseFragment {

    /**
     * Pager
     */
    private ViewPager mViewPager;

    /**
     * VP's adapter
     */
    private PagerAdapter mPagerAdapter;

    private PreferenceUtils mPreferences;

    /**
     * Empty constructor as per the {@link Fragment} documentation
     */
    public MusicBrowserPhoneFragment() {
    }

    @Override
    protected int getLayoutToInflate() {
        return R.layout.fragment_music_browser_phone;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.app_name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the preferences
        mPreferences = PreferenceUtils.getInstance(getActivity());
    }

    @Override
    protected void onViewCreated() {
        super.onViewCreated();

        if (mPagerAdapter == null) {
            // Initialize the adapter
            mPagerAdapter = new PagerAdapter(getActivity(), getChildFragmentManager());
            final MusicFragments[] mFragments = MusicFragments.values();
            for (final MusicFragments mFragment : mFragments) {
                mPagerAdapter.add(mFragment.getFragmentClass(), null);
            }
        }

        // Initialize the ViewPager
        mViewPager = (ViewPager)mRootView.findViewById(R.id.fragment_home_phone_pager);
        // Attch the adapter
        mViewPager.setAdapter(mPagerAdapter);
        // Offscreen pager loading limit
        mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount() - 1);
        // Start on the last page the user was on
        mViewPager.setCurrentItem(mPreferences.getStartPage());

        // Initialze the TPI
        final TabPageIndicator pageIndicator = (TabPageIndicator)mRootView
                .findViewById(R.id.fragment_home_phone_pager_titles);
        // Attach the ViewPager
        pageIndicator.setViewPager(mViewPager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Enable the options menu
        setHasOptionsMenu(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();
        // Save the last page the use was on
        mPreferences.setStartPage(mViewPager.getCurrentItem());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.shuffle_all, menu); // Shuffle all
        if (isArtistPage()) {
            inflater.inflate(R.menu.artist_sort_by, menu);
        } else if (isAlbumPage()) {
            inflater.inflate(R.menu.album_sort_by, menu);
        } else if (isSongPage()) {
            inflater.inflate(R.menu.song_sort_by, menu);
        } else if (isPlaylistPage()) {
            inflater.inflate(R.menu.new_playlist, menu);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_shuffle_all:
                // Shuffle all the songs
                MusicUtils.shuffleAll(getActivity());
                return true;
            case R.id.menu_sort_by_az:
                if (isArtistPage()) {
                    mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_A_Z);
                    getArtistFragment().refresh();
                } else if (isAlbumPage()) {
                    mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_A_Z);
                    getAlbumFragment().refresh();
                } else if (isSongPage()) {
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_A_Z);
                    getSongFragment().refresh();
                }
                return true;
            case R.id.menu_sort_by_za:
                if (isArtistPage()) {
                    mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_Z_A);
                    getArtistFragment().refresh();
                } else if (isAlbumPage()) {
                    mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_Z_A);
                    getAlbumFragment().refresh();
                } else if (isSongPage()) {
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_Z_A);
                    getSongFragment().refresh();
                }
                return true;
            case R.id.menu_sort_by_artist:
                if (isAlbumPage()) {
                    mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_ARTIST);
                    getAlbumFragment().refresh();
                } else if (isSongPage()) {
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_ARTIST);
                    getSongFragment().refresh();
                }
                return true;
            case R.id.menu_sort_by_album:
                if (isSongPage()) {
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_ALBUM);
                    getSongFragment().refresh();
                }
                return true;
            case R.id.menu_sort_by_year:
                if (isAlbumPage()) {
                    mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_YEAR);
                    getAlbumFragment().refresh();
                } else if (isSongPage()) {
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_YEAR);
                    getSongFragment().refresh();
                }
                return true;
            case R.id.menu_sort_by_duration:
                if (isSongPage()) {
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_DURATION);
                    getSongFragment().refresh();
                }
                return true;
            case R.id.menu_sort_by_number_of_songs:
                if (isArtistPage()) {
                    mPreferences
                            .setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_SONGS);
                    getArtistFragment().refresh();
                } else if (isAlbumPage()) {
                    mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_NUMBER_OF_SONGS);
                    getAlbumFragment().refresh();
                }
                return true;
            case R.id.menu_sort_by_number_of_albums:
                if (isArtistPage()) {
                    mPreferences
                            .setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_ALBUMS);
                    getArtistFragment().refresh();
                }
                return true;
            case R.id.menu_sort_by_filename:
                if(isSongPage()) {
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_FILENAME);
                    getSongFragment().refresh();
                }
                return true;
            case R.id.menu_new_playlist:
                if(isPlaylistPage()) {
                    CreateNewPlaylist.getInstance(new long[0]).show(getFragmentManager(), "CreatePlaylist");
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isArtistPage() {
        return mViewPager.getCurrentItem() == MusicFragments.ARTIST.ordinal();
    }

    public ArtistFragment getArtistFragment() {
        return (ArtistFragment)mPagerAdapter.getFragment(MusicFragments.ARTIST.ordinal());
    }

    private boolean isAlbumPage() {
        return mViewPager.getCurrentItem() == MusicFragments.ALBUM.ordinal();
    }

    public AlbumFragment getAlbumFragment() {
        return (AlbumFragment)mPagerAdapter.getFragment(MusicFragments.ALBUM.ordinal());
    }

    private boolean isSongPage() {
        return mViewPager.getCurrentItem() == MusicFragments.SONG.ordinal();
    }

    public SongFragment getSongFragment() {
        return (SongFragment)mPagerAdapter.getFragment(MusicFragments.SONG.ordinal());
    }

    @Override
    public void restartLoader() {
        // do nothing
    }

    private boolean isPlaylistPage() {
        return mViewPager.getCurrentItem() == MusicFragments.PLAYLIST.ordinal();
    }
}
