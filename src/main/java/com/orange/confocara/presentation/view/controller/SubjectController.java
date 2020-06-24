/*
 * Software Name: ConfOCARA
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2020 Orange
 * SPDX-License-Identifier: MPL-2.0
 *
 * This software is distributed under the Mozilla Public License v. 2.0,
 * the text of which is available at http://mozilla.org/MPL/2.0/ or
 * see the "license.txt" file for more details.
 *
 */

package com.orange.confocara.presentation.view.controller;

import com.orange.confocara.business.service.SubjectService;
import com.orange.confocara.connector.persistence.model.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;

@Controller
public class SubjectController {

    public static final String SUBJECT_FILTER_COOKIE = "subjectFilter";
    public static final String USERNAME = "username";
    private static final String SUBJECT = "subject";
    private static final String SUBJECTS = "subjects";
    private static final String ERR_ADD_SUBJECT = "err_add_subject";
    private static final String ID = "id";
    private static final String EDIT_SUBJECT = "editSubject";
    private static final String REDIRECT_ADMIN_SUBJECTS_ADD_SUBJECT = "redirect:/admin/subjects/add-subject";
    private static final String REDIRECT_ADMIN_SUBJECTS = "redirect:/admin/subjects";
    private static final String REDIRECT_ADMIN_SUBJECTS_EDIT_SUBJECT = "redirect:/admin/subjects/edit-subject";
    private static final String CONFLICTUAL_QUESTIONS = "conflictualQuestions";
    private static final String SUBJECT_EDIT_ID_COOKIE = "subjectEditCookieID";

    @Autowired
    private SubjectService subjectService;

    @Secured({"ROLE_SUPERADMIN", "ROLE_ADMIN"})
    @GetMapping("/admin/subjects")
    public String index(Principal principal, Model model) {
        model.addAttribute(USERNAME, principal.getName());
        model.addAttribute(SUBJECTS, subjectService.all());

        return SUBJECTS;
    }

    @GetMapping(value = "/admin/subjects/help")
    public String showHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/subjectsHelper";
    }

    @GetMapping(value = "/admin/subjects/add/help")
    public String showAddHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/addSubjectHelper";
    }

    @GetMapping(value = "/admin/subjects/edit/help")
    public String showEditHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/editSubjectHelper";
    }

    @GetMapping("/admin/subjects/add-subject")
    public String addSubject(Principal principal, Model model) {
        model.addAttribute(USERNAME, principal.getName());

        if (!model.containsAttribute(SUBJECT)) {
            model.addAttribute(SUBJECT, new Subject());
        }

        return "addSubject";
    }

    @GetMapping("/admin/subjects/edit-subject")
    public String editSubject(Principal principal, @Param(value = ID) Long id, @CookieValue(value = SUBJECT_EDIT_ID_COOKIE, required = false) String idCookie, Model model, HttpServletResponse response) {

        if (id != null) {
            response.addCookie(new Cookie(SUBJECT_EDIT_ID_COOKIE, String.valueOf(id)));
        }
        final Long idFinal = id != null ? id : Long.parseLong(idCookie);
        model.addAttribute(USERNAME, principal.getName());

        if (!model.containsAttribute(SUBJECT)) {
            Subject subject = subjectService.withId(idFinal);
            model.addAttribute(SUBJECT, subject);
        }

        model.addAttribute(ID, idFinal);

        return EDIT_SUBJECT;
    }

    @RequestMapping(value = "/admin/subjects/create", method = RequestMethod.POST)
    public String createSubject(@ModelAttribute Subject subject, RedirectAttributes redirectAttributes) {
        if (!subjectService.isAvailable(subject.getName())) {
            redirectAttributes.addFlashAttribute(ERR_ADD_SUBJECT, "");
            Subject invalidSubject = new Subject();
            invalidSubject.setName(subject.getName());
            redirectAttributes.addFlashAttribute(SUBJECT, invalidSubject);

            return REDIRECT_ADMIN_SUBJECTS_ADD_SUBJECT;
        } else {
            subject.setName(subject.getName().trim().replaceAll("\\s+", " "));
            subjectService.create(subject);
            return REDIRECT_ADMIN_SUBJECTS;
        }
    }

    @RequestMapping(value = "/admin/subjects/update", method = RequestMethod.POST)
    public String updateSubject(@ModelAttribute Subject subject, @RequestParam Long id, RedirectAttributes redirectAttributes) {
        if (!(subjectService.withId(id).getName().equalsIgnoreCase(subject.getName().trim().replaceAll("\\s+", " ")) || subjectService.isAvailable(subject.getName()))) {
            redirectAttributes.addFlashAttribute(ERR_ADD_SUBJECT, "");
            redirectAttributes.addAttribute(ID, id);
            Subject invalidSubject = new Subject();
            invalidSubject.setName(subject.getName());
            redirectAttributes.addFlashAttribute(SUBJECT, invalidSubject);
            return REDIRECT_ADMIN_SUBJECTS_EDIT_SUBJECT;
        } else {
            subject.setId(id);
            subject.setName(subject.getName().trim().replaceAll("\\s+", " "));
            subjectService.update(subject);

            return REDIRECT_ADMIN_SUBJECTS;
        }
    }

    @RequestMapping(value = "/admin/subjects/delete", method = RequestMethod.GET)
    public String deleteSubject(@RequestParam(value = ID) Long id, RedirectAttributes redirectAttributes) {
        List<String> conflictualQuestions = subjectService.deleteSubject(id);

        if (!conflictualQuestions.isEmpty()) {
            redirectAttributes.addFlashAttribute(CONFLICTUAL_QUESTIONS, conflictualQuestions);
        }

        return REDIRECT_ADMIN_SUBJECTS;
    }
}
