package com.tencent.cubershi.mock_interface;

import android.animation.Animator;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.os.Bundle;
import android.transition.Transition;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.hydevteam.pluginframework.plugincontainer.PluginContainerActivity;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class ContainerDialogFragment extends DialogFragment implements IContainerFragment {

    private static Map<String, Constructor<?>> constructorMap = new HashMap<>();

    private boolean init = false;

    private OnInflateParams mOnInflateParams;
    private boolean mDestroyed;

    @Override
    public Fragment asFragment() {
        return this;
    }

    private static class OnInflateParams {
        final AttributeSet attrs;
        final Bundle savedInstanceState;

        private OnInflateParams(AttributeSet attrs, Bundle savedInstanceState) {
            this.attrs = attrs;
            this.savedInstanceState = savedInstanceState;
        }
    }

    private static MockDialogFragment instantiatePluginFragment(ContainerDialogFragment containerFragment, Context context) {
        String pluginFragmentClassName = containerFragment.getClass().getName() + "_";
        Constructor<?> constructor = constructorMap.get(pluginFragmentClassName);
        if (constructor == null) {
            PluginContainerActivity containerActivity = (PluginContainerActivity) context;
            PluginActivity pluginActivity = (PluginActivity) containerActivity.getPluginActivity();
            ClassLoader pluginClassLoader = pluginActivity.getClassLoader();
            try {
                Class<?> aClass = pluginClassLoader.loadClass(pluginFragmentClassName);
                constructor = aClass.getConstructor();
                constructorMap.put(pluginFragmentClassName, constructor);
            } catch (Exception e) {
                throw new InstantiationException("无法构造" + pluginFragmentClassName, e);
            }
        }
        try {
            return MockDialogFragment.class.cast(constructor.newInstance());
        } catch (Exception e) {
            throw new InstantiationException("无法构造" + pluginFragmentClassName, e);
        }
    }

    private MockDialogFragment mPluginFragment;

    /**
     * 标志当前Fragment是否由app自己的代码创建的
     */
    private boolean mIsAppCreateFragment = false;

    public MockFragment getPluginFragment() {
        return mPluginFragment;
    }

    @Override
    public void bindPluginFragment(MockFragment pluginFragment) {
        init = true;
        mIsAppCreateFragment = true;
        mPluginFragment = (MockDialogFragment) pluginFragment;
    }

    @Override
    public void unbindPluginFragment() {
        init = false;
        mPluginFragment = null;
    }

    private void initPluginFragment(Context context) {
        if (init) {
            return;
        }
        init = true;

        onBindPluginFragment(context);

        if (mOnInflateParams != null) {
            mPluginFragment.onInflate(mOnInflateParams.attrs, mOnInflateParams.savedInstanceState);
            mOnInflateParams = null;
        }
    }

    private void onBindPluginFragment(Context context) {
        mPluginFragment = instantiatePluginFragment(this, context);
        mPluginFragment.setContainerFragment(this);
    }

    private void onUnbindPluginFragment() {
        mPluginFragment.setContainerFragment(null);
        mPluginFragment = null;
    }

    @Override
    public void onAttach(Context context) {
        initPluginFragment(context);
        super.onAttach(context);

        if (context instanceof PluginContainerActivity) {
            Context pluginActivity = (Context) (((PluginContainerActivity) context).getPluginActivity());
            mPluginFragment.onAttach(pluginActivity);
        }
    }

    @Override
    @Deprecated
    public void onAttach(Activity activity) {
        initPluginFragment(activity);
        super.onAttach(activity);
        if (activity instanceof PluginContainerActivity) {
            Context pluginActivity = (Context) (((PluginContainerActivity) activity).getPluginActivity());
            mPluginFragment.onAttach(pluginActivity);
        }
    }

    @Override
    public String toString() {
        return mPluginFragment.toString();
    }

    @Override
    public void setArguments(Bundle args) {
        if (!mIsAppCreateFragment) {
            mPluginFragment.setArguments(args);
        } else {
            super.setArguments(args);
        }
    }

    @Override
    public void setInitialSavedState(SavedState state) {
        mPluginFragment.setInitialSavedState(state);
    }

    @Override
    public void setTargetFragment(Fragment fragment, int requestCode) {
        mPluginFragment.setTargetFragment(fragment, requestCode);
    }

    @Override
    public Context getContext() {
        return mPluginFragment.getContext();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        mPluginFragment.onHiddenChanged(hidden);
    }

    @Override
    public void setRetainInstance(boolean retain) {
        mPluginFragment.setRetainInstance(retain);
    }

    @Override
    public void setHasOptionsMenu(boolean hasMenu) {
        mPluginFragment.setHasOptionsMenu(hasMenu);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        mPluginFragment.setMenuVisibility(menuVisible);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        mPluginFragment.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public boolean getUserVisibleHint() {
        return mPluginFragment.getUserVisibleHint();
    }

    @Override
    public LoaderManager getLoaderManager() {
        return mPluginFragment.getLoaderManager();
    }

    @Override
    public void startActivity(Intent intent) {
        mPluginFragment.startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        mPluginFragment.startActivity(intent, options);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        mPluginFragment.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        mPluginFragment.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        mPluginFragment.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPluginFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mPluginFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(String permission) {
        return mPluginFragment.shouldShowRequestPermissionRationale(permission);
    }

    @Override
    @Deprecated
    public void onInflate(AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(attrs, savedInstanceState);
        mOnInflateParams = new OnInflateParams(attrs, savedInstanceState);
        if (mPluginFragment != null) {
            mPluginFragment.onInflate(attrs, savedInstanceState);
        }
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        initPluginFragment(context);
        super.onInflate(context, attrs, savedInstanceState);
        mPluginFragment.onInflate(context, attrs, savedInstanceState);
    }

    @Override
    @Deprecated
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        mPluginFragment.onInflate(activity, attrs, savedInstanceState);
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        mPluginFragment.onAttachFragment(childFragment);
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        return mPluginFragment.onCreateAnimator(transit, enter, nextAnim);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPluginFragment.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mPluginFragment.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mPluginFragment.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPluginFragment.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mPluginFragment.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPluginFragment.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPluginFragment.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mPluginFragment.onSaveInstanceState(outState);
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode, Configuration newConfig) {
        mPluginFragment.onMultiWindowModeChanged(isInMultiWindowMode, newConfig);
    }

    @Override
    @Deprecated
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        mPluginFragment.onMultiWindowModeChanged(isInMultiWindowMode);
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        mPluginFragment.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
    }

    @Override
    @Deprecated
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        mPluginFragment.onPictureInPictureModeChanged(isInPictureInPictureMode);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPluginFragment.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPluginFragment.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPluginFragment.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mPluginFragment.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        mPluginFragment.onTrimMemory(level);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPluginFragment.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPluginFragment.onDestroy();
        mDestroyed = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPluginFragment.onDetach();
        if (mDestroyed) {
            onUnbindPluginFragment();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mPluginFragment.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        mPluginFragment.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDestroyOptionsMenu() {
        mPluginFragment.onDestroyOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mPluginFragment.onOptionsItemSelected(item);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        mPluginFragment.onOptionsMenuClosed(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        mPluginFragment.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public void registerForContextMenu(View view) {
        mPluginFragment.registerForContextMenu(view);
    }

    @Override
    public void unregisterForContextMenu(View view) {
        mPluginFragment.unregisterForContextMenu(view);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return mPluginFragment.onContextItemSelected(item);
    }

    @Override
    public void setEnterSharedElementCallback(SharedElementCallback callback) {
        mPluginFragment.setEnterSharedElementCallback(callback);
    }

    @Override
    public void setExitSharedElementCallback(SharedElementCallback callback) {
        mPluginFragment.setExitSharedElementCallback(callback);
    }

    @Override
    public void setEnterTransition(Transition transition) {
        mPluginFragment.setEnterTransition(transition);
    }

    @Override
    public Transition getEnterTransition() {
        return mPluginFragment.getEnterTransition();
    }

    @Override
    public void setReturnTransition(Transition transition) {
        mPluginFragment.setReturnTransition(transition);
    }

    @Override
    public Transition getReturnTransition() {
        return mPluginFragment.getReturnTransition();
    }

    @Override
    public void setExitTransition(Transition transition) {
        mPluginFragment.setExitTransition(transition);
    }

    @Override
    public Transition getExitTransition() {
        return mPluginFragment.getExitTransition();
    }

    @Override
    public void setReenterTransition(Transition transition) {
        mPluginFragment.setReenterTransition(transition);
    }

    @Override
    public Transition getReenterTransition() {
        return mPluginFragment.getReenterTransition();
    }

    @Override
    public void setSharedElementEnterTransition(Transition transition) {
        mPluginFragment.setSharedElementEnterTransition(transition);
    }

    @Override
    public Transition getSharedElementEnterTransition() {
        return mPluginFragment.getSharedElementEnterTransition();
    }

    @Override
    public void setSharedElementReturnTransition(Transition transition) {
        mPluginFragment.setSharedElementReturnTransition(transition);
    }

    @Override
    public Transition getSharedElementReturnTransition() {
        return mPluginFragment.getSharedElementReturnTransition();
    }

    @Override
    public void setAllowEnterTransitionOverlap(boolean allow) {
        mPluginFragment.setAllowEnterTransitionOverlap(allow);
    }

    @Override
    public boolean getAllowEnterTransitionOverlap() {
        return mPluginFragment.getAllowEnterTransitionOverlap();
    }

    @Override
    public void setAllowReturnTransitionOverlap(boolean allow) {
        mPluginFragment.setAllowReturnTransitionOverlap(allow);
    }

    @Override
    public boolean getAllowReturnTransitionOverlap() {
        return mPluginFragment.getAllowReturnTransitionOverlap();
    }

    @Override
    public void postponeEnterTransition() {
        mPluginFragment.postponeEnterTransition();
    }

    @Override
    public void startPostponedEnterTransition() {
        mPluginFragment.startPostponedEnterTransition();
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        mPluginFragment.dump(prefix, fd, writer, args);
    }

}
