package app.xlei.vipexam.feature.wordlist.constant

import app.xlei.vipexam.feature.wordlist.R

enum class SortMethod(val method: Int) {
    OLD_TO_NEW(R.string.sort_by_old_to_new),
    NEW_TO_OLD(R.string.sort_by_new_to_old),
    A_TO_Z(R.string.sort_by_a_z),
    Z_TO_A(R.string.sort_by_z_a),
}