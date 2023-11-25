package com.naohstudio.compass.coreproject.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {

    companion object {

        const val TAG = "naoh"
    }

    open val binding by lazy { bindingView() }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initConfig(savedInstanceState)
        initObserver()
        initListener()
    }

    open fun initConfig(savedInstanceState: Bundle?) {}

    open fun initListener() {}

    open fun initObserver() {}

    abstract fun bindingView(): T
}