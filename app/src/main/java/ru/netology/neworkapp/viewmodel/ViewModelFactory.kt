package ru.netology.neworkapp.viewmodel

//class ViewModelFactory(
//    private val postRepository: PostRepository,
//    private val auth: AppAuth,
//
//) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        when {
//            modelClass.isAssignableFrom(PostViewModel::class.java) ->
//                return PostViewModel(
//                    postRepository,
//                    auth
//                ) as T
//            else ->
//                throw java.lang.IllegalArgumentException("unkown viewmodel ${modelClass.name}")
//        }
//    }
//
//}