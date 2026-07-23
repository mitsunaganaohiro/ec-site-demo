package com.example.demo.controller.admin;

import com.example.demo.common.exception.BusinessException;
import com.example.demo.dto.ProductForm;
import com.example.demo.entity.Product;
import com.example.demo.service.AdminProductService;
import com.example.demo.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final CategoryService categoryService;

    public AdminProductController(AdminProductService adminProductService, CategoryService categoryService) {
        this.adminProductService = adminProductService;
        this.categoryService = categoryService;
    }

    /**
     * API-022: 商品一覧(削除済み含む)。successMessageはRedirectAttributes経由の
     * フラッシュ属性が自動的にModelへ引き継がれるため、ここでの追加処理は不要。
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", adminProductService.findAllIncludingDeleted());
        return "admin/product-list";
    }

    /**
     * API-023: 新規商品登録画面。
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("productForm", new ProductForm());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/product-form";
    }

    /**
     * API-024: 新規商品登録。
     */
    @PostMapping
    public String register(@Valid @ModelAttribute ProductForm productForm, BindingResult bindingResult,
                            Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "admin/product-form";
        }

        adminProductService.register(productForm);
        redirectAttributes.addFlashAttribute("successMessage", "商品を登録しました");
        return "redirect:/admin/products";
    }

    /**
     * API-025: 商品編集画面。存在しない商品はResourceNotFoundExceptionが
     * GlobalExceptionHandlerに委譲され404となる。findById()は削除済みでも例外を
     * スローしないため、ここで直接deletedAtを判定して一覧へ戻す。
     */
    @GetMapping("/{productId}/edit")
    public String editForm(@PathVariable int productId, Model model) {
        Product product = adminProductService.findById(productId);

        if (product.getDeletedAt() != null) {
            return "redirect:/admin/products";
        }

        ProductForm productForm = new ProductForm();
        productForm.setName(product.getName());
        productForm.setPrice(product.getPrice());
        productForm.setCategoryId(product.getCategoryId());
        productForm.setStockQuantity(product.getStockQuantity());

        model.addAttribute("productForm", productForm);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("productId", productId);
        return "admin/product-form";
    }

    /**
     * API-026: 商品更新。
     */
    @PostMapping("/{productId}")
    public String update(@PathVariable int productId, @Valid @ModelAttribute ProductForm productForm,
                          BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("productId", productId);
            return "admin/product-form";
        }

        try {
            adminProductService.update(productId, productForm);
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/products";
        }

        redirectAttributes.addFlashAttribute("successMessage", "商品を更新しました");
        return "redirect:/admin/products";
    }

    /**
     * API-027: 商品削除(論理削除)。存在しない商品はResourceNotFoundExceptionが
     * GlobalExceptionHandlerに委譲され404となる。
     */
    @PostMapping("/{productId}/delete")
    public String delete(@PathVariable int productId, RedirectAttributes redirectAttributes) {
        adminProductService.softDelete(productId);
        redirectAttributes.addFlashAttribute("successMessage", "商品を削除しました");
        return "redirect:/admin/products";
    }
}
