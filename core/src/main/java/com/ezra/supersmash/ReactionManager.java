package com.ezra.supersmash;

import com.ezra.supersmash.Effects.BurnEffect;

public class ReactionManager {

    /**
     * Memproses reaksi antara elemen yang sudah ada di target (baseElement)
     * dan elemen dari serangan yang masuk (triggerElement).
     * @return ReactionResult yang berisi pengganda damage dan pesan.
     */
    public static ReactionResult processReaction(Element baseElement, Element triggerElement) {
        // Jika tidak ada elemen dasar di target, tidak ada reaksi.
        if (baseElement == Element.NEUTRAL) {
            return new ReactionResult(1.0f, ""); // 1.0f multiplier = tidak ada perubahan damage
        }

        // --- DEFINISI REAKSI ---

        // Reaksi 1: Vaporize (Air + Api) -> Damage besar
        if (baseElement == Element.WATER && triggerElement == Element.FIRE) {
            return new ReactionResult(2.0f, "Vaporize! Critical Hit!");
        }

        // Reaksi 2: Burning (Alam + Api) -> Menghasilkan efek status Burn
        if (baseElement == Element.NATURE && triggerElement == Element.FIRE) {
            // Damage tidak digandakan, tetapi menghasilkan efek Burn
            return new ReactionResult(1.0f, "Burning!", new BurnEffect(2, 10));
        }

        // Reaksi 3: Overgrow (Alam + Air) -> Target menyembuhkan diri
        if (baseElement == Element.NATURE && triggerElement == Element.WATER) {
            // Di sini kita bisa membuat efek Heal, untuk sekarang kita return pesan saja
            return new ReactionResult(0.5f, "Overgrow! Damage reduced!"); // Damage dikurangi
        }


        // Jika tidak ada kombinasi reaksi yang cocok, tidak ada reaksi
        return new ReactionResult(1.0f, "");
    }
}
